package com.ym.classroomassignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class ExcelUtils {

  public static List<Student> mapExcelToStudents(MultipartFile file) throws IOException {
    List<Student> students = new ArrayList<>();

    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 가져오기

      for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
          continue;
        }

        // 셀 데이터 읽기
        String code = getCellValueAsString(row.getCell(0));
        if (!StringUtils.hasText(code)) {
          continue;
        }

        int grade = (int) getCellValueAsDouble(row.getCell(1));
        int clazz = (int) getCellValueAsDouble(row.getCell(2));
        int number = (int) getCellValueAsDouble(row.getCell(3));
        String gender = getCellValueAsString(row.getCell(4));

        int humanities1 = (int) getCellValueAsDouble(row.getCell(16));
        int humanities2 = (int) getCellValueAsDouble(row.getCell(17));
        int humanities3 = (int) getCellValueAsDouble(row.getCell(18));
        int humanities4 = (int) getCellValueAsDouble(row.getCell(19));
        int humanities5 = (int) getCellValueAsDouble(row.getCell(20));
        int humanities6 = (int) getCellValueAsDouble(row.getCell(21));
        int humanitiesCount =
            humanities1 + humanities2 + humanities3 + humanities4 + humanities5 + humanities6;

        int naturalCount1 = (int) getCellValueAsDouble(row.getCell(22));
        int naturalCount2 = (int) getCellValueAsDouble(row.getCell(23));
        int naturalCount3 = (int) getCellValueAsDouble(row.getCell(24));
        int naturalCount4 = (int) getCellValueAsDouble(row.getCell(25));
        int naturalCount5 = (int) getCellValueAsDouble(row.getCell(26));
        int naturalCount =
            naturalCount1 + naturalCount2 + naturalCount3 + naturalCount4 + naturalCount5;

        // Student 객체 생성 및 추가
        students.add(
            new Student(code, grade, clazz, number, gender, humanitiesCount, naturalCount));
      }
    }

    return students;
  }

  private static String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return "";
    }
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case FORMULA -> cell.getCellFormula();
      default -> "";
    };
  }

  private static double getCellValueAsDouble(Cell cell) {
    if (cell == null) {
      return 0;
    }

    switch (cell.getCellType()) {
      case NUMERIC:
        return cell.getNumericCellValue();
      case STRING:
        // 문자열을 숫자로 변환
        try {
          return Double.parseDouble(cell.getStringCellValue());
        } catch (NumberFormatException e) {
          return 0; // 변환 실패 시 기본값 반환
        }
      default:
        return 0; // 그 외의 타입은 0 반환
    }
  }

}
