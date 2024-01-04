package util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ExcelData {
    private Workbook workbook;
    private Sheet sheet;
    private String filePath;
    
    private static final String[] FORM_HEADERS = {
        "Timestamp", "Name", "Email", "Phone", "National ID", "IBAN", 
        "Message", "Issue Type", "Category", "Sub-Category", 
        "Privacy Consent", "Status", "Response Time (ms)", "API Status Code", "Notes"
    };

    public ExcelData(String filePath, String sheetName) {
    	
        this.filePath = filePath;
        try {
            workbook = getWorkbook(filePath);
            sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Workbook getWorkbook(String filePath) throws IOException {
        Workbook workbook;
        try (InputStream inputStream = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            workbook = new XSSFWorkbook();
        }
        return workbook;
    }
    
    public void writeFormSubmission(Map<String, String> formData) {
        synchronized (this) {
            try (FileInputStream fis = new FileInputStream(filePath)) {
                Workbook wb = new XSSFWorkbook(fis);
                Sheet sh = wb.getSheet(sheet.getSheetName());
                
                if (sh == null) {
                    sh = wb.createSheet(sheet.getSheetName());
                }
                
                if (sh.getPhysicalNumberOfRows() == 0) {
                    Row headerRow = sh.createRow(0);
                    for (int i = 0; i < FORM_HEADERS.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(FORM_HEADERS[i]);
                        
                        CellStyle headerStyle = wb.createCellStyle();
                        Font headerFont = wb.createFont();
                        headerFont.setBold(true);
                        headerStyle.setFont(headerFont);
                        cell.setCellStyle(headerStyle);
                    }
                }
                
                int nextRow = sh.getLastRowNum() + 1;
                Row dataRow = sh.createRow(nextRow);
                
                String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                
                dataRow.createCell(0).setCellValue(timestamp);
                dataRow.createCell(1).setCellValue(formData.getOrDefault("fullName", ""));
                dataRow.createCell(2).setCellValue(formData.getOrDefault("email", ""));
                dataRow.createCell(3).setCellValue(formData.getOrDefault("phoneNumber", ""));
                dataRow.createCell(4).setCellValue(formData.getOrDefault("nationalId", ""));
                dataRow.createCell(5).setCellValue(formData.getOrDefault("iban", ""));
                dataRow.createCell(6).setCellValue(formData.getOrDefault("message", ""));
                dataRow.createCell(7).setCellValue(formData.getOrDefault("subject", ""));
                dataRow.createCell(8).setCellValue(formData.getOrDefault("category", ""));
                dataRow.createCell(9).setCellValue(formData.getOrDefault("subCategory", ""));
                dataRow.createCell(10).setCellValue(formData.getOrDefault("privacyPolicyConsent", ""));
                dataRow.createCell(11).setCellValue(formData.getOrDefault("status", ""));
                dataRow.createCell(12).setCellValue(formData.getOrDefault("responseTime", ""));
                dataRow.createCell(13).setCellValue(formData.getOrDefault("apiStatusCode", ""));
                dataRow.createCell(14).setCellValue(formData.getOrDefault("notes", ""));
                
                for (int i = 0; i < FORM_HEADERS.length; i++) {
                    sh.autoSizeColumn(i);
                }
                
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    wb.write(fos);
                }
                
                wb.close();
                
            } catch (FileNotFoundException e) {
                createNewFormSubmissionFile(formData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void createNewFormSubmissionFile(Map<String, String> formData) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Submissions");
            
            Row headerRow = sh.createRow(0);
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < FORM_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(FORM_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }
            
            Row dataRow = sh.createRow(1);
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            dataRow.createCell(0).setCellValue(timestamp);
            dataRow.createCell(1).setCellValue(formData.getOrDefault("fullName", ""));
            dataRow.createCell(2).setCellValue(formData.getOrDefault("email", ""));
            dataRow.createCell(3).setCellValue(formData.getOrDefault("phoneNumber", ""));
            dataRow.createCell(4).setCellValue(formData.getOrDefault("nationalId", ""));
            dataRow.createCell(5).setCellValue(formData.getOrDefault("iban", ""));
            dataRow.createCell(6).setCellValue(formData.getOrDefault("message", ""));
            dataRow.createCell(7).setCellValue(formData.getOrDefault("subject", ""));
            dataRow.createCell(8).setCellValue(formData.getOrDefault("category", ""));
            dataRow.createCell(9).setCellValue(formData.getOrDefault("subCategory", ""));
            dataRow.createCell(10).setCellValue(formData.getOrDefault("privacyPolicyConsent", ""));
            dataRow.createCell(11).setCellValue(formData.getOrDefault("status", ""));
            dataRow.createCell(12).setCellValue(formData.getOrDefault("responseTime", ""));
            dataRow.createCell(13).setCellValue(formData.getOrDefault("apiStatusCode", ""));
            dataRow.createCell(14).setCellValue(formData.getOrDefault("notes", ""));
            
            for (int i = 0; i < FORM_HEADERS.length; i++) {
                sh.autoSizeColumn(i);
            }
            
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Map<String, String> readLastSubmission() {
        Map<String, String> submissionData = new HashMap<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {
            
            Sheet sh = wb.getSheetAt(0);
            int lastRow = sh.getLastRowNum();
            
            if (lastRow < 1) {
                return submissionData;
            }
            
            Row dataRow = sh.getRow(lastRow);
            Row headerRow = sh.getRow(0);
            
            for (int i = 0; i < FORM_HEADERS.length; i++) {
                Cell headerCell = headerRow.getCell(i);
                Cell dataCell = dataRow.getCell(i);
                
                if (headerCell != null && dataCell != null) {
                    String header = headerCell.getStringCellValue();
                    String value = getCellValueAsString(dataCell);
                    submissionData.put(header, value);
                }
            }
            
            
        } catch (IOException e) {
            System.err.println("Failed to read Excel file: " + e.getMessage());
        }
        
        return submissionData;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    public int getSubmissionCount() {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {
            
            Sheet sh = wb.getSheetAt(0);
            return sh.getLastRowNum();
            
        } catch (IOException e) {
            return 0;
        }
    }
}
