package util;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UserData {
    private String[][] data;

    public UserData() throws IOException {
        String excelFilePath = System.getProperty("user.dir") + "/src/main/java/selenium/resources/userdata.xlsx";
        FileInputStream inputStream = new FileInputStream(excelFilePath);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheet("Sheet1");

        int rowCount = sheet.getLastRowNum() + 1;
        int colCount = sheet.getRow(0).getLastCellNum();

        data = new String[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < colCount; j++) {
                Cell cell = row.getCell(j);
                data[i][j] = cell.getStringCellValue();
            }
        }

        workbook.close();
        inputStream.close();
    }

    public String[] getUserDataByType(String type) {
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equalsIgnoreCase(type)) {
                return data[i];
            }
        }
        return null;
    }
}
