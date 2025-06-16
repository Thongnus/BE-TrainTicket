package com.example.betickettrain.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;
import java.util.Map;

public class XlssfHelper {
    public  static void createTable(XSSFSheet sheet, List<Map<String, Object>> data, List<String> columns) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            header.createCell(i).setCellValue(columns.get(i));
        }

        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> rowMap = data.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columns.size(); j++) {
                Object val = rowMap.get(columns.get(j));
                row.createCell(j).setCellValue(val != null ? val.toString() : "");
            }
        }
    }
    public static void createTableFromArray(XSSFSheet sheet, List<List<String>> headers, Map<String, Object> data) {
        Row headerRow = sheet.createRow(0);
        List<String> keys = headers.get(0);
        for (int i = 0; i < keys.size(); i++) {
            headerRow.createCell(i).setCellValue(keys.get(i));
        }

        int rowCount = ((List<?>) data.get(keys.get(0))).size();

        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < keys.size(); j++) {
                Object value = ((List<?>) data.get(keys.get(j))).get(i);
                row.createCell(j).setCellValue(value != null ? value.toString() : "");
            }
        }
    }

}
