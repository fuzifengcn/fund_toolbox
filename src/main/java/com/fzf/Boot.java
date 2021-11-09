package com.fzf;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Boot {

    static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    static final String FUND_CODE_FILE_NAME = "fundCodes.txt";
    static final String LOG_FILE_NAME = "log.txt";
    static final String CURRENT_PATH = System.getProperty("user.dir");

    static final String SEARCH_FUND_NAME_URL = "https://fundsuggest.eastmoney.com/FundSearch/api/FundSearchAPI.ashx";

    static final String SEARCH_FUND_NAV_URL = "https://api.fund.eastmoney.com/f10/lsjz";

    static final Map<String, String> HEADER = new HashMap<>();


    static final String FIRST_START = "-01-01";
    static final String FIRST_END = "-03-31";
    static final String SECOND_START = "-04-01";
    static final String SECOND_END = "-06-30";
    static final String THIRD_START = "-07-01";
    static final String THIRD_END = "-09-30";
    static final String FOURTH_START = "-10-01";
    static final String FOURTH_END = "-12-31";

    static {
        HEADER.put("Referer", "https://fundf10.eastmoney.com/");
    }


    public static void main(String[] args) throws FileNotFoundException {
//        System.setOut(new PrintStream(new FileOutputStream(CURRENT_PATH.concat(File.separator).concat(LOG_FILE_NAME))));
        boot();
    }

    private static void boot() {

        String[] fundCodes = readFundCodeFromFile();
        List<List<String>> data = new ArrayList<>();
        for (String fundCode : fundCodes) {
            List<String> row = new ArrayList<>();
            String[] fundName = searchFundNameAndNAVByFundCode(fundCode);

            row.add(fundCode);
            row.add(fundName[0]);
            row.add(fundName[1] );
            if ("0".equals(fundName[0])){
                data.add(row);
                continue;
            }
            row.add(searchCurrentMonthNAV(fundCode));

//            System.out.print(fundCode + "\t\t" + fundName[0] + "\t\t" + fundName[1] + "\t\t" + searchCurrentMonthNAV(fundCode) + "\t\t");
            Calendar instance = Calendar.getInstance();
            int i = instance.get(Calendar.YEAR);
            for (int j = 0; j < 3; j++) {
                row.add(searchFourthNAV(fundCode, (i - j) + ""));
                row.add(searchThirdNAV(fundCode, (i - j) + ""));
                row.add(searchSecondNAV(fundCode, (i - j) + ""));
                row.add(searchFirstNAV(fundCode, (i - j) + ""));

//                System.out.print(searchFirstNAV(fundCode, (i - j) + "") + "\t\t");
//                System.out.print(searchSecondNAV(fundCode, (i - j) + "") + "\t\t");
//                System.out.print(searchThirdNAV(fundCode, (i - j) + "") + "\t\t");
//                System.out.print(searchFourthNAV(fundCode, (i - j) + "") + "\t\t");
            }
            System.out.println();
            data.add(row);

        }

        String format = new SimpleDateFormat("yyyy-MM-dd-HHmmssSSS").format(new Date());
        EasyExcel.write(CURRENT_PATH.concat(File.separator).concat(format).concat(".xls")).sheet("sheet1").doWrite(data);
    }

    public static String searchFirstNAV(String fundCode, String year) {

        try {
            return searchFundNAV(fundCode, SIMPLE_DATE_FORMAT.parse(year + FIRST_START), SIMPLE_DATE_FORMAT.parse(year + FIRST_END));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }

    public static String searchSecondNAV(String fundCode, String year) {

        try {
            return searchFundNAV(fundCode, SIMPLE_DATE_FORMAT.parse(year + SECOND_START), SIMPLE_DATE_FORMAT.parse(year + SECOND_END));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }

    public static String searchThirdNAV(String fundCode, String year) {

        try {
            return searchFundNAV(fundCode, SIMPLE_DATE_FORMAT.parse(year + THIRD_START), SIMPLE_DATE_FORMAT.parse(year + THIRD_END));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }

    public static String searchFourthNAV(String fundCode, String year) {

        try {
            return searchFundNAV(fundCode, SIMPLE_DATE_FORMAT.parse(year + FOURTH_START), SIMPLE_DATE_FORMAT.parse(year + FOURTH_END));
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }


    public static String searchCurrentMonthNAV(String fundCode) {
        // 本月起始
        Calendar thisMonthFirstDateCal = Calendar.getInstance();
        thisMonthFirstDateCal.set(Calendar.DAY_OF_MONTH, 1);

        // 本月末尾
        Calendar thisMonthEndDateCal = Calendar.getInstance();
        thisMonthEndDateCal.set(Calendar.DAY_OF_MONTH, thisMonthEndDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return searchFundNAV(fundCode, thisMonthFirstDateCal.getTime(), thisMonthEndDateCal.getTime());
    }


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
            BigDecimal subtract = maxNAV.subtract(minNAV)
                    .divide(minNAV, 5, BigDecimal.ROUND_DOWN)
                    .multiply(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_DOWN);
            return subtract.toString();
        }
        return "error";

    }


    private static String[] searchFundNameAndNAVByFundCode(String fundCode) {
        if (StringUtils.isBlank(fundCode)) {
            return new String[]{"", ""};
        }
// https://fundsuggest.eastmoney.com/FundSearch/api/FundSearchAPI.ashx?m=1&key=002190&_=1636467561122
        Map<String, String> param = new HashMap<>();
        param.put("m", "1");
        param.put("key", fundCode);
        String responseStr = OkHttpUtils.get(SEARCH_FUND_NAME_URL, param);
        if (StringUtils.isNotBlank(responseStr)) {
            JSONObject jsonObject = JSONObject.parseObject(responseStr);
            JSONArray datas = jsonObject.getJSONArray("Datas");
            if (datas.size() > 1) {
                JSONObject fundInfo = datas.getJSONObject(0);
                if (fundCode.equals(fundInfo.getString("CODE")) && "700".equals(fundInfo.getString("CATEGORY"))) {
                    JSONObject fundBaseInfo = fundInfo.getJSONObject("FundBaseInfo");
                    String currentDateStr = SIMPLE_DATE_FORMAT.format(new Date());
                    if (fundBaseInfo.getString("FSRQ").equals(currentDateStr)) {
                        return new String[]{fundInfo.getString("NAME"), fundBaseInfo.getString("DWJZ")};
                    }
                    return new String[]{fundInfo.getString("NAME"), fundBaseInfo.getString("DWJZ").concat(" ").concat(fundBaseInfo.getString("FSRQ"))};
                }
            }

        }

        return new String[]{"0", "fund code not found : ".concat(fundCode)};

    }

    private static String[] readFundCodeFromFile() {

        String fundCodeFilePath = CURRENT_PATH.concat(File.separator).concat(FUND_CODE_FILE_NAME);
        File fundCodeFile = new File(fundCodeFilePath);
        if (!fundCodeFile.exists()) {
            System.err.println("fund code file not exist!");
        }

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(fundCodeFile);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strTmp = "";
            while ((strTmp = bufferedReader.readLine()) != null) {
                stringBuilder.append(strTmp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String s = stringBuilder.toString()
                .replaceAll("\\\n", "")
                .replaceAll("\\r\\n", "")
                .replaceAll("，", ",").trim();

        return s.split(",");
    }


}
