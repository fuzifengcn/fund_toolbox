package com.fzf;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Boot {

    // 获取当前程序执行路径
    static final String CURRENT_PATH = System.getProperty("user.dir");
    static final String DATA_PATH_NAME = CURRENT_PATH.concat(File.separator).concat("data").concat(File.separator);
    static final String LOG_PATH_NAME = CURRENT_PATH.concat(File.separator).concat("log").concat(File.separator);
    static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final String FUND_CODE_FILE_NAME = CURRENT_PATH.concat(File.separator).concat("fundCodes.txt");
    static final String LOG_FILE_NAME = LOG_PATH_NAME.concat("log.txt");

    // 查询基金名称的url
    static final String SEARCH_FUND_NAME_URL = "https://fundsuggest.eastmoney.com/FundSearch/api/FundSearchAPI.ashx";

    //查询历史净值的url
    static final String SEARCH_FUND_NAV_URL = "https://api.fund.eastmoney.com/f10/lsjz";

    static final Map<String, String> HEADER = new HashMap<>();

    static  final  List<List<String>> TABLE_HEAD = new ArrayList<>();

    // 区分季度
    static final String FIRST_START = "-01-01";
    static final String FIRST_END = "-03-31";
    static final String SECOND_START = "-04-01";
    static final String SECOND_END = "-06-30";
    static final String THIRD_START = "-07-01";
    static final String THIRD_END = "-09-30";
    static final String FOURTH_START = "-10-01";
    static final String FOURTH_END = "-12-31";

    // 获取历史净值http请求头 和 excel表头
    static {
        HEADER.put("Referer", "https://fundf10.eastmoney.com/");
        TABLE_HEAD.add(Collections.singletonList("基金代码"));
        TABLE_HEAD.add(Collections.singletonList("基金名称"));
        TABLE_HEAD.add(Collections.singletonList("当日净值"));
        TABLE_HEAD.add(Collections.singletonList("上一完整自然月涨幅"));
        TABLE_HEAD.add(Collections.singletonList("上一完整季度涨幅"));
        TABLE_HEAD.add(Collections.singletonList("当日净值日期"));
    }


    //启动类 程序入口
    public static void main(String[] args){
        try {
            init();
            System.out.println(YYYY_MM_DD_HH_MM_SS.format(new Date()) + " start");
            boot();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(YYYY_MM_DD_HH_MM_SS.format(new Date()) + " end");


    }

    // 初始化系统设置  与业务不相关
    private static void init() {


        File dataPath = new File(DATA_PATH_NAME);
        if(!dataPath.exists()){
            dataPath.mkdirs();
        }
        File file = new File(LOG_PATH_NAME);
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            PrintStream out = new PrintStream(new FileOutputStream(LOG_FILE_NAME));
            System.setOut(out);
            System.setErr(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    // 业务启动类  所有流程在此类完成
    private static void boot() {


        String[] fundCodes ;
        try {
            // 读取 fundCode中的基金代码
            fundCodes = readFundCodeFromFile();
        } catch (IOException e) {
            System.out.println("fund code not found!");
            return;
        }
        List<FundInfo> data = new ArrayList<>();
        // 遍历基金代码
        for (String fundCode : fundCodes) {
            FundInfo row = new FundInfo();
            // 查询 基金名称 当日净值
            String[] fundName = searchFundNameAndNAVByFundCode(fundCode);

            row.setFundCode(fundCode);
            row.setFundName(fundName[0]);
            row.setCurrentNAV(fundName[1] );
            if ("0".equals(fundName[0])){
                data.add(row);
                continue;
            }
            // 计算上一个月的涨幅
            String monthNAV = searchLastMonthNAV(fundCode);
            row.setLastMonthNAV(monthNAV);

            Calendar instance = Calendar.getInstance();
            // 获取今年的年份 例如 ：2021
            int year = instance.get(Calendar.YEAR);
            // 获取当月的月份
            int month = instance.get(Calendar.MONTH);
            // 判断当前月份是第几季度
            switch (month){
                case 1: case 2: case 3:
                    // 1-3月 计算上年第四季度涨幅 下面依次类推
                    row.setLastQuarterlyNAV(searchFourthNAV(fundCode, (year-1) + ""));
                    break;
                case 4: case 5: case 6:
                    row.setLastQuarterlyNAV(searchFirstNAV(fundCode, (year) + ""));
                    break;
                case 7: case 8: case 9:
                    row.setLastQuarterlyNAV(searchSecondNAV(fundCode, (year) + ""));
                    break;
                case 10: case 11:case 12:
                    row.setLastQuarterlyNAV(searchThirdNAV(fundCode, (year) + ""));
                    break;

            }
            row.setCurrentDate(fundName[2]);
            data.add(row);

        }
        FundInfo row2  = new FundInfo();
        row2.setFundCode(" ");
        data.add(row2);
        FundInfo row  = new FundInfo();
        row.setFundCode("截至时间");
        row.setFundName(YYYY_MM_DD_HH_MM_SS.format(new Date()));
        data.add(row);
        String format = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date());
        // 写入excel文件
        EasyExcel.write(DATA_PATH_NAME.concat(format).concat(".xlsx"),FundInfo.class)
                .sheet("sheet1")
                .doWrite(data);
    }

    // 计算第1季度的涨幅
    public static String searchFirstNAV(String fundCode, String year) {

        try {
            return searchFundNAV(fundCode, SIMPLE_DATE_FORMAT.parse(year + FIRST_START), SIMPLE_DATE_FORMAT.parse(year + FIRST_END));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }
    // 计算第2季度的涨幅
    public static String searchSecondNAV(String fundCode, String year) {

        try {
            return searchFundNAV(fundCode, SIMPLE_DATE_FORMAT.parse(year + SECOND_START), SIMPLE_DATE_FORMAT.parse(year + SECOND_END));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }
    // 计算第3季度的涨幅
    public static String searchThirdNAV(String fundCode, String year) {

        try {
            return searchFundNAV(fundCode, SIMPLE_DATE_FORMAT.parse(year + THIRD_START), SIMPLE_DATE_FORMAT.parse(year + THIRD_END));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }
    // 计算第4季度的涨幅
    public static String searchFourthNAV(String fundCode, String year) {

        try {
            return searchFundNAV(fundCode, SIMPLE_DATE_FORMAT.parse(year + FOURTH_START), SIMPLE_DATE_FORMAT.parse(year + FOURTH_END));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }

    // 计算本月涨幅
    public static String searchCurrentMonthNAV(String fundCode) {
        // 本月起始
        Calendar thisMonthFirstDateCal = Calendar.getInstance();
        thisMonthFirstDateCal.set(Calendar.DAY_OF_MONTH, 1);

        // 本月末尾
        Calendar thisMonthEndDateCal = Calendar.getInstance();
        thisMonthEndDateCal.set(Calendar.DAY_OF_MONTH, thisMonthEndDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return searchFundNAV(fundCode, thisMonthFirstDateCal.getTime(), thisMonthEndDateCal.getTime());
    }

    // 计算上一个月涨幅
    public static String searchLastMonthNAV(String fundCode) {
        Calendar start  = Calendar.getInstance();
        start.add(Calendar.MONTH,-1);
        start.set(Calendar.DAY_OF_MONTH,1);
        Calendar end  = Calendar.getInstance();
        end.set(Calendar.DAY_OF_MONTH,0);

        return searchFundNAV(fundCode, start.getTime(), end.getTime());
    }


//    计算两个日期之间的涨幅
    public static String searchFundNAV(String fundCode, Date startDate, Date endDate) {
        String startDateStr = SIMPLE_DATE_FORMAT.format(startDate);
        String endDateStr = SIMPLE_DATE_FORMAT.format(endDate);
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        int reduceDay = end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        Map<String, String> param = new HashMap<>();
        param.put("fundCode", fundCode);
        param.put("pageSize", reduceDay + "");
        param.put("pageIndex", "1");
        param.put("startDate", startDateStr);
        param.put("endDate", endDateStr);
        // 发动网络请求查询历史净值列表
        String responseStr = OkHttpUtils.get(SEARCH_FUND_NAV_URL, param, HEADER);
        if (StringUtils.isNotBlank(responseStr)) {
            JSONArray jsonArray = JSON.parseObject(responseStr).getJSONObject("Data").getJSONArray("LSJZList");
            if (jsonArray == null || jsonArray.size() < 1) {
                return "-";
            }
            JSONObject max = jsonArray.getJSONObject(0);
            JSONObject min = jsonArray.getJSONObject(jsonArray.size() - 1);
            BigDecimal maxNAV = new BigDecimal(max.getString("DWJZ"));
            BigDecimal minNAV = new BigDecimal(min.getString("DWJZ"));
            // 计算涨幅
            BigDecimal subtract = maxNAV.subtract(minNAV)
                    .divide(minNAV, 5, BigDecimal.ROUND_DOWN)
                    .multiply(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_DOWN);
            return subtract.toString();
        }
        return "error";

    }

    // https://fundsuggest.eastmoney.com/FundSearch/api/FundSearchAPI.ashx?m=1&key=002190&_=1636467561122
    // 查询基金名称和当日净值  返回值  第一个位置：基金名  第二位置：基金当日净值  第三个位置：净值日期
    private static String[] searchFundNameAndNAVByFundCode(String fundCode) {

        if (StringUtils.isBlank(fundCode)) {
            return new String[]{"", "", ""};
        }
        Map<String, String> param = new HashMap<>();
        param.put("m", "1");
        param.put("key", fundCode);
        String responseStr = OkHttpUtils.get(SEARCH_FUND_NAME_URL, param);
        if (StringUtils.isNotBlank(responseStr)) {
            JSONObject jsonObject = JSONObject.parseObject(responseStr);
            JSONArray datas = jsonObject.getJSONArray("Datas");
            if (datas.size() > 0) {
                JSONObject fundInfo = datas.getJSONObject(0);
                if (fundCode.equals(fundInfo.getString("CODE")) && "700".equals(fundInfo.getString("CATEGORY"))) {
                    JSONObject fundBaseInfo = fundInfo.getJSONObject("FundBaseInfo");
                    String currentDateStr = SIMPLE_DATE_FORMAT.format(new Date());
                    if (fundBaseInfo.getString("FSRQ").equals(currentDateStr)) {
                        return new String[]{fundInfo.getString("NAME"), fundBaseInfo.getString("DWJZ"),""};
                    }
                    return new String[]{fundInfo.getString("NAME"), fundBaseInfo.getString("DWJZ"),fundBaseInfo.getString("FSRQ")};
                }
            }

        }

        return new String[]{"0", "fund code not found : ".concat(fundCode),""};

    }

    // 读取基金代码从文件中
    private static String[] readFundCodeFromFile() throws IOException {

        File fundCodeFile = new File(FUND_CODE_FILE_NAME);
        if (!fundCodeFile.exists()) {
            System.err.println("fund code file not exist!");
        }

        StringBuilder stringBuilder = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream(fundCodeFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String strTmp = "";
        while ((strTmp = bufferedReader.readLine()) != null) {
            stringBuilder.append(strTmp);
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = stringBuilder.toString()
                .replaceAll("\\\n", "")
                .replaceAll("\\r\\n", "")
                .replaceAll("，", ",").trim();

        return s.split(",");
    }


}
