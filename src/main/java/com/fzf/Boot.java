package com.fzf;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.math.BigDecimal;
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

    //查询历史净值的url
    static final String SEARCH_FUND_NAV_URL = "https://api.fund.eastmoney.com/f10/lsjz";

    static final String FUND_INFO_PAGE_URL = "https://fund.eastmoney.com/";

    static final Map<String, String> HEADER = new HashMap<>();

    static Document FUND_INFO_HTML_DOCUMENT;

    static String FUND_CODE;

    static int CURRENT_YEAR;
    static int CURRENT_MONTH;
    static String LAST_QUARTERLY_START;
    static String LAST_QUARTERLY_END;
    static String LAST_MONTH_START;
    static String CURRENT_MONTH_START;
    static String LAST_MONTH_END;

    static FundInfo FUND_INFO;
    static final String[] QUARTERLY_START_MAPPER = new String[4];
    static final String[] QUARTERLY_END_MAPPER = new String[4];
    static final List<FundInfo> ROWS = new ArrayList<>();
    static final List<JSONObject> QUARTERLY_DATA_LIST = new ArrayList<>();
    static final List<JSONObject> LAST_MONTH_DATA_LIST = new ArrayList<>();
    static final List<JSONObject> CURRENT_MONTH_DATA_LIST = new ArrayList<>();
    static String[] FUND_CODE_LIST;
    // 获取历史净值http请求头 和 excel表头
    static {
        HEADER.put("Referer", "https://fundf10.eastmoney.com/");
    }


    //init method
    private static void initTime() {
        Calendar instance = Calendar.getInstance();
        CURRENT_YEAR = instance.get(Calendar.YEAR);
        CURRENT_MONTH = instance.get(Calendar.MONTH);
        QUARTERLY_START_MAPPER[0] = (CURRENT_YEAR - 1) + "-10-01";
        QUARTERLY_START_MAPPER[1] = CURRENT_YEAR + "-01-01";
        QUARTERLY_START_MAPPER[2] = CURRENT_YEAR + "-04-01";
        QUARTERLY_START_MAPPER[3] = CURRENT_YEAR + "-07-01";
        QUARTERLY_END_MAPPER[0] = (CURRENT_YEAR - 1) + "-12-31";
        QUARTERLY_END_MAPPER[1] = CURRENT_YEAR + "-03-31";
        QUARTERLY_END_MAPPER[2] = CURRENT_YEAR + "-06-30";
        QUARTERLY_END_MAPPER[3] = CURRENT_YEAR + "-09-30";
        LAST_QUARTERLY_START = QUARTERLY_START_MAPPER[CURRENT_MONTH / 3];
        LAST_QUARTERLY_END = QUARTERLY_END_MAPPER[CURRENT_MONTH / 3];
        instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 1);
        CURRENT_MONTH_START = SIMPLE_DATE_FORMAT.format(instance.getTime());
        instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, -1);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        LAST_MONTH_START = SIMPLE_DATE_FORMAT.format(instance.getTime());
        instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 0);
        LAST_MONTH_END = SIMPLE_DATE_FORMAT.format(instance.getTime());
    }
    // 初始化系统设置  与业务不相关
    private static void initLogConfig() {
        File dataPath = new File(DATA_PATH_NAME);
        if (!dataPath.exists()) {
            dataPath.mkdirs();
        }
        File file = new File(LOG_PATH_NAME);
        if (!file.exists()) {
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
    // 初始化历史净值数据并分组
    private static void initBaseFundInfo() {
        if(LAST_MONTH_DATA_LIST.size()>0){
            QUARTERLY_DATA_LIST.clear();
            LAST_MONTH_DATA_LIST.clear();
            CURRENT_MONTH_DATA_LIST.clear();
        }

        String url = FUND_INFO_PAGE_URL.concat(FUND_CODE).concat(".html").concat("?spm=search");
        String htmlStr = OkHttpUtils.get(url, null);
        FUND_INFO_HTML_DOCUMENT = Jsoup.parse(htmlStr);


        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {
            start.setTime(SIMPLE_DATE_FORMAT.parse(LAST_QUARTERLY_START));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        end.setTime(new Date());

        int reduceDay = end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        Map<String, String> param = new HashMap<>();
        param.put("fundCode", FUND_CODE);
        param.put("pageSize", reduceDay + "");
        param.put("pageIndex", "1");
        param.put("startDate", LAST_QUARTERLY_START);
        param.put("endDate", SIMPLE_DATE_FORMAT.format(new Date()));
        String responseStr = OkHttpUtils.get(SEARCH_FUND_NAV_URL, param, HEADER);
        if (StringUtils.isNotBlank(responseStr)) {
            JSONArray jsonArray = JSON.parseObject(responseStr).getJSONObject("Data").getJSONArray("LSJZList");
            if (jsonArray != null && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dateStr = jsonObject.getString("FSRQ");
                    if (LAST_QUARTERLY_START.compareTo(dateStr) <= 0
                            && LAST_QUARTERLY_END.compareTo(dateStr) >= 0) {
                        QUARTERLY_DATA_LIST.add(jsonObject);
                    }
                    if (LAST_MONTH_START.compareTo(dateStr) <= 0
                            && LAST_MONTH_END.compareTo(dateStr) >= 0) {
                        LAST_MONTH_DATA_LIST.add(jsonObject);
                    }

                    if (CURRENT_MONTH_START.compareTo(dateStr) <= 0) {
                        CURRENT_MONTH_DATA_LIST.add(jsonObject);
                    }

                }

            }
        }
    }


    //启动类 程序入口
    public static void main(String[] args) {
        try {
            initLogConfig();
            System.out.println(YYYY_MM_DD_HH_MM_SS.format(new Date()) + " start");
            initTime();
            initFundCodes();
            boot();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(YYYY_MM_DD_HH_MM_SS.format(new Date()) + " end");

    }





    // 业务启动类  所有流程在此类完成
    private static void boot() {
        // 遍历基金代码
        for (String fundCode : FUND_CODE_LIST) {
            FUND_CODE = fundCode;
            FUND_INFO = new FundInfo(fundCode);
            initBaseFundInfo();
            setFundName();
            setCurrentNAV();
            setCurrentMonthNAV();
            setLastMonthNAV();
            setCurrentMonthNAV();
            setCreateDate();
            setLastQuarterlyNAV();
            ROWS.add(FUND_INFO);

        }
        FundInfo row2 = new FundInfo();
        row2.setFundCode(" ");
        ROWS.add(row2);
        FundInfo row = new FundInfo();
        row.setFundCode("截至时间");
        row.setFundName(YYYY_MM_DD_HH_MM_SS.format(new Date()));
        ROWS.add(row);
        String format = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date());
        // 写入excel文件
        EasyExcel.write(DATA_PATH_NAME.concat(format).concat(".xlsx"), FundInfo.class)
                .registerWriteHandler(new MyCellStyleHandler())
                .sheet("sheet1")
                .doWrite(ROWS);
    }
    // 获取成立日期
    private static void setCreateDate() {
        Elements infoOfFund = FUND_INFO_HTML_DOCUMENT.getElementsByClass("infoOfFund");
        Element element = infoOfFund.get(0);
        Elements trs = element.getElementsByTag("tr");
        Element tr = trs.get(1);
        Element td = tr.getElementsByTag("td").get(0);
        FUND_INFO.setCreatAt(td.text().split("：")[1]);
    }
    // 获取基金净值
    private static void setCurrentNAV() {

        Elements infoOfFund = FUND_INFO_HTML_DOCUMENT.getElementsByClass("dataItem02");
        if (infoOfFund.size() > 0) {
            Element element = infoOfFund.get(0);
            Element p = element.getElementsByTag("dt").get(0).getElementsByTag("p").get(0);
            Elements span = p.getElementsByTag("span");
            span.remove();
            FUND_INFO.setCurrentDate(p.text().replaceAll("\\)", ""));
            Element nav = element.getElementsByTag("dd").get(0).getElementsByTag("span").get(0);
            FUND_INFO.setCurrentNAV(nav.text());
        } else {
            FUND_INFO.setCurrentNAV("基金异常");
        }
    }
    // 获取基金名称
    private static void setFundName() {
        Elements infoOfFund = FUND_INFO_HTML_DOCUMENT.getElementsByClass("fundDetail-tit");
        Element div = infoOfFund.get(0).getElementsByTag("div").get(0);
        div.getElementsByTag("span").remove();
        FUND_INFO.setFundName(div.text());
    }

    // 计算上季度涨幅
    private static void setLastQuarterlyNAV() {
        FUND_INFO.setLastQuarterlyNAV(getNav(QUARTERLY_DATA_LIST));
    }

    // 计算本月涨幅
    public static void setCurrentMonthNAV() {
        FUND_INFO.setCurrentMonthNAV(getNav(CURRENT_MONTH_DATA_LIST));
    }

    // 计算上一个月涨幅
    public static void setLastMonthNAV() {
        FUND_INFO.setLastMonthNAV(getNav(LAST_MONTH_DATA_LIST));
    }

    private static void initFundCodes(){
        try {
            // 读取 fundCode中的基金代码
            FUND_CODE_LIST = readFundCodeFromFile();
        } catch (IOException e) {
            System.out.println("fund code not found!");
        }
    }
    // 计算涨幅
    private static String getNav(List<JSONObject> list){
        if (list.size() > 0) {
            JSONObject max = list.get(0);
            JSONObject min = list.get(list.size() - 1);
            BigDecimal maxNAV = new BigDecimal(max.getString("DWJZ"));
            BigDecimal minNAV = new BigDecimal(min.getString("DWJZ"));
            // 计算涨幅
            BigDecimal nav = maxNAV.subtract(minNAV)
                    .divide(minNAV, 5, BigDecimal.ROUND_DOWN)
                    .multiply(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_DOWN);
            return nav.toString();
        }else{
            return "-";
        }
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
