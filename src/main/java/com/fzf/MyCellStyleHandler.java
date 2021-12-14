package com.fzf;

import com.alibaba.excel.util.BooleanUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.*;

public class MyCellStyleHandler implements CellWriteHandler {


    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {

        Cell cell = context.getCell();
        if(BooleanUtils.isFalse(context.getHead()) &&( cell.getColumnIndex() > 3 && cell.getColumnIndex() < 6)){

            Workbook workbook = context.getWriteSheetHolder().getSheet().getWorkbook();
            CellStyle cellStyle = workbook.createCellStyle();

            String stringCellValue = cell.getStringCellValue();
            if(stringCellValue.startsWith("-")){
                Font font = workbook.createFont();
                font.setColor(IndexedColors.GREEN.getIndex());
                cellStyle.setFont(font);
                cell.setCellStyle(cellStyle);
            }


        }

    }
}
