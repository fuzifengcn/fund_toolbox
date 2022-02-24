package com.fzf.fund;

public class EastMoneyDataSource implements DataSource{


    //history nav api url
    static final String SEARCH_FUND_NAV_URL = "https://api.fund.eastmoney.com/f10/lsjz";

    //data source domain
    static final String FUND_INFO_PAGE_URL = "https://fund.eastmoney.com/";


    @Override
    public void crawFundBasicInfo(FundContext fundContext) {

    }

    @Override
    public void crawFundNavHistoryData(FundContext fundContext) {

    }
}
