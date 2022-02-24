package com.fzf.fund;

public interface DataSource {

    void crawFundBasicInfo(FundContext fundContext);


    void crawFundNavHistoryData(FundContext fundContext);


}
