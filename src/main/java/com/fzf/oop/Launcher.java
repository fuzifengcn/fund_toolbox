package com.fzf.oop;

import com.fzf.oop.base.FundContextRuntimeConfig;
import com.fzf.oop.classic.ClassicFundContext;
import com.fzf.oop.data.EasyMoneyDataSource;

public class Launcher {

    public static final String VERSION = "3.0.0";

    public static void main(String[] args) {
        FundContextRuntimeConfig fundContextRuntimeConfig = new FundContextRuntimeConfig();
        ClassicFundContext classicFundContext = new ClassicFundContext(fundContextRuntimeConfig,new EasyMoneyDataSource());
        classicFundContext.start();
    }
}
