package com.fzf;

import com.alibaba.excel.util.BooleanUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MyCellStyleHandler implements CellWriteHandler {

    static Workbook workbook;
    static Map<String, CellStyle> styleMap = new HashMap<>();

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {

        Cell cell = context.getCell();
        if (workbook == null) {
            workbook = cell.getSheet().getWorkbook();
        }
        String stringCellValue = cell.getStringCellValue();
        if (BooleanUtils.isNotTrue(context.getHead())) {
            if ("".equals(stringCellValue) || stringCellValue == null ) {
                return;
            }
            context.getFirstCellData().setWriteCellStyle(null);
            switch (cell.getColumnIndex()) {
                case 3:
                    if (new BigDecimal(1).compareTo(new BigDecimal(stringCellValue)) > 0) {
                        cell.setCellStyle(getCurrentNAVBackground());
                    }else {
                        cell.setCellStyle(getFontStyle());
                    }
                    break;
                case 4: case 5: case 6: case 7:
                    int compareTo = new BigDecimal(0).compareTo(new BigDecimal(stringCellValue));
                    if (compareTo > 0) {
                        cell.setCellStyle(getGreenFontStyle());
                    } else if (compareTo < 0) {
                        cell.setCellStyle(getRedFontStyle());
                    } else {
                        cell.setCellStyle(getFontStyle());
                    }
                    break;
                default:
                    cell.setCellStyle(getFontStyle());
            }
        }
    }

    public CellStyle getRedFontStyle() {
        CellStyle red = styleMap.get("red");
        if (red == null) {
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Times New Roman");
            font.setColor(IndexedColors.RED.getIndex());
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            styleMap.put("red", cellStyle);
            red = cellStyle;
        }
        return red;
    }

    public CellStyle getGreenFontStyle() {
        CellStyle green = styleMap.get("green");
        if (green == null) {
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Times New Roman");
            font.setColor(IndexedColors.GREEN.getIndex());
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            styleMap.put("green", cellStyle);
            green = cellStyle;
        }
        return green;
    }

    public CellStyle getCurrentNAVBackground() {
        CellStyle currentNavBackground = styleMap.get("currentNavBackground");
        if (currentNavBackground == null) {
            CellStyle cellStyle = workbook.createCellStyle();
            HSSFWorkbook hssfWorkbook = (HSSFWorkbook) workbook;
            HSSFPalette customPalette = hssfWorkbook.getCustomPalette();
            customPalette.setColorAtIndex(IndexedColors.GREEN.index,(byte)0,(byte)204,(byte)0);
            Font font = MyCellStyleHandler.workbook.createFont();
            font.setFontName("Times New Roman");
            cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFont(font);
            styleMap.put("currentNavBackground", cellStyle);
            currentNavBackground = cellStyle;
        }
        return currentNavBackground;
    }

    public CellStyle getFontStyle() {
        CellStyle fontStyle = styleMap.get("fontStyle");
        if (fontStyle == null) {
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Times New Roman");
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFont(font);
            styleMap.put("fontStyle", cellStyle);
            fontStyle = cellStyle;
        }
        return fontStyle;
    }

}
