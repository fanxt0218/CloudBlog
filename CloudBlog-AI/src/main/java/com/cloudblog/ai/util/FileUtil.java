package com.cloudblog.ai.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FileUtil {

    /**
     * 提取 Excel 文件中的文本内容
     * @param inputStream 文件输入流
     * @return 提取的文本内容
     * @throws IOException 读取文件时发生异常
     */
    public static String extractExcelText(InputStream inputStream) throws IOException {
        try {
            StringBuilder content = new StringBuilder();

            // 使用 Apache POI 处理 Excel 文件
            org.apache.poi.ss.usermodel.Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(inputStream);

            // 遍历所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(i);
                content.append("工作表: ").append(sheet.getSheetName()).append("\n");

                // 遍历行
                for (org.apache.poi.ss.usermodel.Row row : sheet) {
                    // 遍历单元格
                    for (org.apache.poi.ss.usermodel.Cell cell : row) {
                        String cellValue = getCellValueAsString(cell);
                        content.append(cellValue).append("\t");
                    }
                    content.append("\n");
                }
                content.append("\n");
            }

            workbook.close();
            return content.toString();
        } catch (Exception e) {
            log.error("Failed to extract text from Excel file: {}", e.getMessage(), e);
            return "无法解析 Excel 文件: " + e.getMessage();
        }
    }

    /**
     * 将单元格值转换为字符串
     * @param cell 单元格对象
     * @return 单元格的字符串表示
     */
    private static String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
