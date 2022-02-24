package com.fzf.fund;

import java.util.Date;

public class FundHistoryNavOriginalDataDTO {

    private String fundCode;
    private String navDateStr;
    private Date navDate;
    private String navValue;


    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getNavDateStr() {
        return navDateStr;
    }

    public void setNavDateStr(String navDateStr) {
        this.navDateStr = navDateStr;
    }

    public Date getNavDate() {
        return navDate;
    }

    public void setNavDate(Date navDate) {
        this.navDate = navDate;
    }

    public String getNavValue() {
        return navValue;
    }

    public void setNavValue(String navValue) {
        this.navValue = navValue;
    }

    @Override
    public String toString() {
        return "FundHistoryNAVOriginalDTO{" +
                "fundCode='" + fundCode + '\'' +
                ", navDateStr='" + navDateStr + '\'' +
                ", navDate=" + navDate +
                ", navValue='" + navValue + '\'' +
                '}';
    }
}
