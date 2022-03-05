package com.fzf.oop.base;

public class BaseFundInfo {

    private String fundCode;
    private String fundName;
    private String fundCreatedAt;
    private String currentDayNav;
    private String currentDate;


    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getFundCreatedAt() {
        return fundCreatedAt;
    }

    public void setFundCreatedAt(String fundCreatedAt) {
        this.fundCreatedAt = fundCreatedAt;
    }

    public String getCurrentDayNav() {
        return currentDayNav;
    }

    public void setCurrentDayNav(String currentDayNav) {
        this.currentDayNav = currentDayNav;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
