package com.fzf.oop.base;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.fzf.oop.classic.MyCellStyleHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class FundContext<T> {

    private final FundContextRuntimeConfig contextRuntimeConfig;
    private final FundDataSource fundDataSource;
    private List<FundCodes> fundCodes;
    static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    int CURRENT_YEAR;
    int CURRENT_MONTH;
    String LAST_QUARTERLY_START;
    String LAST_QUARTERLY_END;
    String LAST_MONTH_START;
    String CURRENT_MONTH_START;
    String LAST_MONTH_END;
    static final String[] QUARTERLY_START_MAPPER = new String[4];
    static final String[] QUARTERLY_END_MAPPER = new String[4];


    public FundContext(FundContextRuntimeConfig contextRuntimeConfig, FundDataSource fundDataSource) {
        this.contextRuntimeConfig = contextRuntimeConfig;
        this.fundDataSource = fundDataSource;
    }

    protected abstract CellWriteHandler getCellWriteHandler();

    public void start() {
        initEnvironment();
        loadFundCodes();
        initDateConfig();
        exportData();
    }

    private void initDateConfig() {
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

    private void loadFundCodes() {
        fundCodes = TextFileFundCodeLoader.load(this);
    }

    public abstract Class<T> getExportEntityClass();

    private void exportData() {
        for (FundCodes fundCode : fundCodes) {
            List<T> data = new ArrayList<>();
            String[] fundCodes = fundCode.getFundCodes();
            if (fundCodes != null && fundCodes.length > 0) {
                for (String code : fundCodes) {
                    BaseFundInfo baseFundInfo = fundDataSource.getBaseFundInfo(code);
                    List<BaseFundHistoryNavInfo> navInfos = fundDataSource.getHistoryNavList(getHistoryNavStartDate(),
                            getHistoryNavEndDate(), code);
                    //TODO sort historyNav
                    data.addAll(processData(baseFundInfo, navInfos));
                }
            }
            sort(data);
            EasyExcel.write(contextRuntimeConfig.getDataPath()
                    .concat(fundCode.getFileName()
                            .substring(0, fundCode.getFileName().lastIndexOf(".")))
                    .concat(".xls"), getExportEntityClass())
                    .registerWriteHandler(getCellWriteHandler())
                    .sheet("sheet1")
                    .doWrite(data);
        }


    }

    protected void sort(List<T> data){

    }

    protected abstract String getHistoryNavStartDate();

    protected abstract String getHistoryNavEndDate();

    protected abstract List<T> processData(BaseFundInfo baseFundInfo, List<BaseFundHistoryNavInfo> navInfos);

    private void initEnvironment() {
        File dataPath = new File(contextRuntimeConfig.getDataPath());
        if (!dataPath.exists()) {
            boolean mkdirs = dataPath.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("data file save path create fail!");
            }
        }
        if (!contextRuntimeConfig.isDEBUG()) {
            File file = new File(contextRuntimeConfig.getLogPath());
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    throw new RuntimeException("log file save path create fail!");
                }
            }
            try {
                PrintStream out = new PrintStream(new FileOutputStream(contextRuntimeConfig.getLogPath().concat("log.txt")));
                System.setOut(out);
                System.setErr(out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public FundContextRuntimeConfig getContextRuntimeConfig() {
        return contextRuntimeConfig;
    }

    public List<FundCodes> getFundCodes() {
        return fundCodes;
    }

    public FundDataSource getFundDataSource() {
        return fundDataSource;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return SIMPLE_DATE_FORMAT;
    }

    public int getCURRENT_YEAR() {
        return CURRENT_YEAR;
    }

    public int getCURRENT_MONTH() {
        return CURRENT_MONTH;
    }

    public String getLAST_QUARTERLY_START() {
        return LAST_QUARTERLY_START;
    }

    public String getLAST_QUARTERLY_END() {
        return LAST_QUARTERLY_END;
    }

    public String getLAST_MONTH_START() {
        return LAST_MONTH_START;
    }

    public String getCURRENT_MONTH_START() {
        return CURRENT_MONTH_START;
    }

    public String getLAST_MONTH_END() {
        return LAST_MONTH_END;
    }

    public static String[] getQuarterlyStartMapper() {
        return QUARTERLY_START_MAPPER;
    }

    public static String[] getQuarterlyEndMapper() {
        return QUARTERLY_END_MAPPER;
    }
}
