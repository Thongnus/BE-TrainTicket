package com.example.betickettrain.util;

import com.example.betickettrain.exceptions.BusinessException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public static LocalDateTime getDateTimeFromCell(Cell cell, int rowNumber, String fieldName) {
        if (cell == null) {
            throw new BusinessException("Dòng " + (rowNumber + 1) + ": " + fieldName + " không được để trống.");
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String text = cell.getStringCellValue().trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(text, formatter);
            } else {
                throw new BusinessException("Dòng " + (rowNumber + 1) + ": " + fieldName + " không đúng định dạng.");
            }
        } catch (Exception e) {
            throw new BusinessException("Dòng " + (rowNumber + 1) + ": lỗi parse " + fieldName + " → " + e.getMessage());
        }
    }

}
