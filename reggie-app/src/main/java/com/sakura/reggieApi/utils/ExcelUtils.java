package com.sakura.reggieApi.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelUtils {


    public static void modifyStyle(HSSFCell cell, Object val, HSSFWorkbook workbook) {
        // 设置正文字体格式
        HSSFFont dataSetFont = workbook.createFont();
        dataSetFont.setBold(false);

        // 此处设置数据格式
        HSSFDataFormat df = workbook.createDataFormat();

        // 创建文本样式
        HSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setBorderBottom(BorderStyle.THIN);
        textStyle.setBorderRight(BorderStyle.THIN);
        textStyle.setBorderLeft(BorderStyle.THIN);
        textStyle.setFont(dataSetFont);
        // 数据内容对齐方式：居左
        textStyle.setAlignment(HorizontalAlignment.LEFT);

        // 创建浮点型数字样式
        HSSFCellStyle floatStyle = workbook.createCellStyle();
        floatStyle.setBorderBottom(BorderStyle.THIN);
        floatStyle.setBorderRight(BorderStyle.THIN);
        floatStyle.setBorderLeft(BorderStyle.THIN);
        floatStyle.setAlignment(HorizontalAlignment.RIGHT);
        floatStyle.setFont(dataSetFont);
        floatStyle.setDataFormat(df.getFormat("#,##0.00"));

        // 创建整型数字样式
        HSSFCellStyle integerStyle = workbook.createCellStyle();
        integerStyle.setBorderBottom(BorderStyle.THIN);
        integerStyle.setBorderRight(BorderStyle.THIN);
        integerStyle.setBorderLeft(BorderStyle.THIN);
        integerStyle.setAlignment(HorizontalAlignment.RIGHT);
        integerStyle.setFont(dataSetFont);
        integerStyle.setDataFormat(df.getFormat("0"));


        if (val == null)
            return;

        String dataType = val.getClass().getName();
        if (dataType.endsWith("BigDecimal") || dataType.endsWith("Double") || dataType.endsWith("Float")) {
            cell.setCellStyle(floatStyle);  // 带小数点的数字格式
            cell.setCellValue(Double.parseDouble(val.toString()));
        } else if (dataType.endsWith("Integer") || dataType.endsWith("Long")) {
            cell.setCellStyle(integerStyle);    // 整型数字格式
            cell.setCellValue(Double.parseDouble(val.toString()));
        } else if (dataType.endsWith("Date")) {
            cell.setCellStyle(textStyle);   //日期转为字符串
            cell.setCellValue(date2Str((Date) val, "yyyy-MM-dd HH:mm:ss"));
        } else {
            cell.setCellStyle(textStyle);   // 文本格式
            cell.setCellValue(val.toString());
        }
    }

    private static String date2Str(Date val, String s) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(s);

        return dateFormat.format(val);
    }
}
