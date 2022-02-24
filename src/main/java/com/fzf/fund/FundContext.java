package com.fzf.fund;

import java.io.*;
import java.util.List;

public class FundContext {
    private static String[] FUND_CODE_LIST;
    private DataSource dataSource;
    private List<FundCalculator> calculators;


    static String CURRENT_PATH;
    static String DATA_PATH_NAME;
    static String LOG_PATH_NAME;
    static String FUND_CODE_FILE_NAME;
    static String LOG_FILE_NAME;
//    static SimpleDateFormat SIMPLE_DATE_FORMAT;
//    static SimpleDateFormat YYYY_MM_DD_HH_MM_SS;
//
//    static int CURRENT_YEAR;
//    static int CURRENT_MONTH;
//    static String LAST_QUARTERLY_START;
//    static String LAST_QUARTERLY_END;
//    static String LAST_MONTH_START;
//    static String CURRENT_MONTH_START;
//    static String LAST_MONTH_END;
//    static final String[] QUARTERLY_START_MAPPER = new String[4];
//    static final String[] QUARTERLY_END_MAPPER = new String[4];


    public final void start() {
        initEnvironment();
        dataSource.crawFundBasicInfo(this);
        dataSource.crawFundNavHistoryData(this);
        dataProcess();
        dataPostProcess();
    }

    private void dataPostProcess() {
    }

    private void dataProcess() {
        for (FundCalculator calculator : calculators) {

        }

    }

    public final FundContext addCalculator(FundCalculator fundCalculator){
        this.calculators.add(fundCalculator);
        return this;
    }

    public static String[] getFundCodeList() {
        return FUND_CODE_LIST;
    }

    protected FundContext(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    private void initEnvironment() {
        initAppRuntime();
//        initDateBoundary();
        loadFundCodeFile();

    }

    private void loadFundCodeFile() {
        setFundCodeList(readFundCodeFromFile());
    }

    private static String[] readFundCodeFromFile() {
        File fundCodeFile = new File(FUND_CODE_FILE_NAME);
        if (!fundCodeFile.exists()) {
            System.err.println("fund code file not exist!");
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(fundCodeFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strTmp;
            while ((strTmp = bufferedReader.readLine()) != null) {
                stringBuilder.append(strTmp);
            }
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

    private FundContext() {
    }



    private void initAppRuntime() {
        CURRENT_PATH = System.getProperty("user.dir");
        DATA_PATH_NAME = CURRENT_PATH.concat(File.separator).concat("data").concat(File.separator);
        LOG_PATH_NAME = CURRENT_PATH.concat(File.separator).concat("log").concat(File.separator);
        FUND_CODE_FILE_NAME = CURRENT_PATH.concat(File.separator).concat("fundCodes.txt");
        LOG_FILE_NAME = LOG_PATH_NAME.concat("log.txt");
//        SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//        YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

//    private void initDateBoundary() {
//        Calendar instance = Calendar.getInstance();
//        CURRENT_YEAR = instance.get(Calendar.YEAR);
//        CURRENT_MONTH = instance.get(Calendar.MONTH);
//        QUARTERLY_START_MAPPER[0] = (CURRENT_YEAR - 1) + "-10-01";
//        QUARTERLY_START_MAPPER[1] = CURRENT_YEAR + "-01-01";
//        QUARTERLY_START_MAPPER[2] = CURRENT_YEAR + "-04-01";
//        QUARTERLY_START_MAPPER[3] = CURRENT_YEAR + "-07-01";
//        QUARTERLY_END_MAPPER[0] = (CURRENT_YEAR - 1) + "-12-31";
//        QUARTERLY_END_MAPPER[1] = CURRENT_YEAR + "-03-31";
//        QUARTERLY_END_MAPPER[2] = CURRENT_YEAR + "-06-30";
//        QUARTERLY_END_MAPPER[3] = CURRENT_YEAR + "-09-30";
//        LAST_QUARTERLY_START = QUARTERLY_START_MAPPER[CURRENT_MONTH / 3];
//        LAST_QUARTERLY_END = QUARTERLY_END_MAPPER[CURRENT_MONTH / 3];
//        instance = Calendar.getInstance();
//        instance.set(Calendar.DAY_OF_MONTH, 1);
//        CURRENT_MONTH_START = SIMPLE_DATE_FORMAT.format(instance.getTime());
//        instance = Calendar.getInstance();
//        instance.add(Calendar.MONTH, -1);
//        instance.set(Calendar.DAY_OF_MONTH, 1);
//        LAST_MONTH_START = SIMPLE_DATE_FORMAT.format(instance.getTime());
//        instance = Calendar.getInstance();
//        instance.set(Calendar.DAY_OF_MONTH, 0);
//        LAST_MONTH_END = SIMPLE_DATE_FORMAT.format(instance.getTime());
//    }

    private static void setFundCodeList(String[] fundCodeList) {
        FUND_CODE_LIST = fundCodeList;
    }
}
