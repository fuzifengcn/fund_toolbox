package com.fzf.oop.classic;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

public class ClassicFundInfo {

    private static final short HEAD_FONT_SIZE = 10;
    private static final String HEADER_FONT_NAME = "Times New Roman";

    ClassicFundInfo(){
        this.remarkInfo = "";
    }

    @ExcelProperty(value = "基金代码",index = 0)
    @ColumnWidth(15)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE,fontName = HEADER_FONT_NAME)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String fundCode;

    @ColumnWidth(50)
    @ExcelProperty(value = "基金名称",index = 1)
    private String fundName;

    @ColumnWidth(25)
    @ExcelProperty(value = "成立日期")
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String creatAt;

    @ColumnWidth(15)
    @ExcelProperty(value = "当日净值")
    private String currentNAV;

    @ColumnWidth(20)
    @ExcelProperty(value = "前日净值涨幅（%）")
    private String lastDayNAV;

    @ColumnWidth(20)
    @ExcelProperty(value = "当月净值涨幅（%）")
    private String currentMonthNAV;

    @ColumnWidth(20)
    @ExcelProperty(value = "上月净值涨幅（%）")
    private String lastMonthNAV;

    @ColumnWidth(24)
    @ExcelProperty(value = "上季度净值涨幅（%）")
    private String lastQuarterlyNAV;

    @ColumnWidth(13)
    @ExcelProperty(value = "当日净值时间")
    private String currentDate;

    @ColumnWidth(20)
    @ExcelProperty(value = "备注")
    private String remarkInfo;


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

    public String getCurrentNAV() {
        return currentNAV;
    }

    public void setCurrentNAV(String currentNAV) {
        this.currentNAV = currentNAV;
    }

    public String getLastMonthNAV() {
        return lastMonthNAV;
    }

    public void setLastMonthNAV(String lastMonthNAV) {
        this.lastMonthNAV = lastMonthNAV;
    }

    public String getLastQuarterlyNAV() {
        return lastQuarterlyNAV;
    }

    public void setLastQuarterlyNAV(String lastQuarterlyNAV) {
        this.lastQuarterlyNAV = lastQuarterlyNAV;
    }

    public String getCurrentMonthNAV() {
        return currentMonthNAV;
    }

    public void setCurrentMonthNAV(String currentMonthNAV) {
        this.currentMonthNAV = currentMonthNAV;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(String creatAt) {
        this.creatAt = creatAt;
    }

    public String getLastDayNAV() {
        return lastDayNAV;
    }

    public void setLastDayNAV(String lastDayNAV) {
        this.lastDayNAV = lastDayNAV;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getRemarkInfo() {
        return remarkInfo;
    }

    public void setRemarkInfo(String remarkInfo) {
        this.remarkInfo = remarkInfo;
    }
}
