package com.fzf;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

public class FundInfo {

    private static final short HEAD_FONT_SIZE = 11;



    @ExcelProperty("基金代码")
    @ColumnWidth(15)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String fundCode;

    @ColumnWidth(25)
    @ExcelProperty("基金名称")
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String fundName;

    @ColumnWidth(25)
    @ExcelProperty("成立日期")
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String creatAt;

    @ColumnWidth(15)
    @ExcelProperty("当日净值")
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String currentNAV;

    @ColumnWidth(20)
    @ExcelProperty("当月净值涨幅（%）")
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String currentMonthNAV;

    @ColumnWidth(20)
    @ExcelProperty("上月净值涨幅（%）")
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String lastMonthNAV;

    @ColumnWidth(20)
    @ExcelProperty("上季度净值涨幅（%）")
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    private String lastQuarterlyNAV;

    @ColumnWidth(13)
    @ExcelProperty("当日净值时间")
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @HeadFontStyle(fontHeightInPoints = HEAD_FONT_SIZE,bold = BooleanEnum.TRUE)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
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

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
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
}
