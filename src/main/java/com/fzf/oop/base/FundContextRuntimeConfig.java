package com.fzf.oop.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FundContextRuntimeConfig {
    static final SimpleDateFormat yyMMddHHmm = new SimpleDateFormat("yyMMddHHmm");

     private final String CURRENT_PATH;
     private final String DATA_PATH_NAME;
     private final String LOG_PATH_NAME;
     private final String FUND_CODE_FILE_PATH;
     private final String FUND_CODE_FILE_PREFIX;
     private final boolean DEBUG;

    public FundContextRuntimeConfig(){
        this.CURRENT_PATH = System.getProperty("user.dir");
        this.DATA_PATH_NAME = CURRENT_PATH.concat(File.separator).concat("data").concat(File.separator)
                .concat(File.separator).concat(yyMMddHHmm.format(new Date())).concat(File.separator);
        this.LOG_PATH_NAME = CURRENT_PATH.concat(File.separator).concat("log").concat(File.separator);
        this.FUND_CODE_FILE_PATH  = CURRENT_PATH.concat(File.separator).concat("fundCode");
        this.FUND_CODE_FILE_PREFIX = "FC";
        this.DEBUG = true;
    }

    public String getPath(){
        return CURRENT_PATH;
    }

    public String getDataPath(){
        return DATA_PATH_NAME;
    }

    public String getLogPath(){
        return LOG_PATH_NAME;
    }

    public String getFundCodeFilePath() {
        return FUND_CODE_FILE_PATH;
    }

    public String getFundCodeFilePrefix(){
        return FUND_CODE_FILE_PREFIX;
    }

    public boolean isDEBUG() {
        return DEBUG;
    }
}
