package com.dsc.spos.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExcelUtils {

    private static final String path = "";

    private static final String EXT = ".xlsx";

    public static String convertToExcel(List<Map<String, Object>> data, String fileName) throws Exception {

        String filePath = path + File.pathSeparator+ fileName + EXT;

        // 1. 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(fileName);

        // 2. 创建表头
        if (!data.isEmpty()) {
            Set<String> headers = data.get(0).keySet();
            Row headerRow = sheet.createRow(0);

            int colIndex = 0;
            for (String header : headers) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(header);
            }

            // 3. 填充数据
            int rowIndex = 1;
            for (Map<String, Object> rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (String header : headers) {
                    Cell cell = row.createCell(colIndex++);
                    Object value = rowData.get(header);
                    setCellValue(cell, value);
                }
            }
        }

        // 4. 自动调整列宽
        for (int i = 0; i < data.get(0).size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // 5. 写入文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }

        workbook.close();

        return filePath;
    }

    public static String convertToExcelWithDirectory(List<Map<String, Object>> data, String fileName,String directory) throws Exception {

        String filePath = System.getProperty("catalina.home") + "\\webapps\\"+directory+"\\";
        File file = new File(filePath);
        // 如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
        file = null;
        filePath +=  fileName + EXT;

        // 1. 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(fileName);

        // 2. 创建表头
        if (!data.isEmpty()) {
            Set<String> headers = data.get(0).keySet();
            Row headerRow = sheet.createRow(0);

            int colIndex = 0;
            for (String header : headers) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(header);
            }

            // 3. 填充数据
            int rowIndex = 1;
            for (Map<String, Object> rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (String header : headers) {
                    Cell cell = row.createCell(colIndex++);
                    Object value = rowData.get(header);
                    setCellValue(cell, value);
                }
            }
        }

        // 4. 自动调整列宽
        for (int i = 0; i < data.get(0).size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // 5. 写入文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }

        workbook.close();

        return filePath;
    }


    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
            style.setDataFormat((short)14); // 日期格式
            cell.setCellStyle(style);
        } else {
            cell.setCellValue(value.toString());
        }
    }

}
