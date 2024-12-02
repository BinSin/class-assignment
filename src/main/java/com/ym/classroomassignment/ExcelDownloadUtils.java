package com.ym.classroomassignment;

import com.ym.classroomassignment.dto.Student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class ExcelDownloadUtils {

  private static final List<String> FRONT_HEADER = List.of("학년", "반", "번호", "이름", "성별");
  private static final List<String> BACK_HEADER = List.of("선택 과목 수");

  public static StreamingResponseBody getExcelOutputStream(
      List<List<Student>> classes, List<String> allHeaders, List<Integer> humanities,
      List<Integer> sciences) {
    return outputStream -> {
      try (Workbook workbook = new XSSFWorkbook()) {

        String[] etcHeaders = {"반", "여학생 수", "남학생 수", "1과목 선택 수", "2과목 선택 수", "3과목 선택 수",
            "4과목 선택 수", "5과목 선택 수", "6과목 선택 수"};

        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        int resultMapIndex = 1;
        for (int i = resultMapIndex; i <= classes.size(); i++) {
          resultMap.put(i, new ArrayList<>());
        }

        // 각 반별로 시트 생성 및 데이터 추가
        for (int nextClassNumber = 0; nextClassNumber < classes.size(); nextClassNumber++) {
          List<Integer> results = resultMap.get(resultMapIndex);

          List<Student> classStudents = classes.get(nextClassNumber);

          // 시트 생성
          Sheet sheet = workbook.createSheet((nextClassNumber + 1) + "반");

          CellStyle headerStyle = createCellStyle(workbook, IndexedColors.YELLOW);

          // 헤더 작성
          Row headerRow = sheet.createRow(0);

          boolean isScience = classStudents.get(1).getScienceSubjects().size() >= 2;

          String[] headers;
          if (isScience) {
            headers = getHeaders(allHeaders, sciences);
          } else {
            headers = getHeaders(allHeaders, humanities);
          }
          for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);

            sheet.setColumnWidth(i, 9 * 256);
          }

          CellStyle defaultStyle = createCellStyle(workbook, IndexedColors.WHITE);
          CellStyle blueStyle = createCellStyle(workbook, IndexedColors.LIGHT_BLUE);
          CellStyle greenStyle = createCellStyle(workbook, IndexedColors.LIGHT_GREEN);
          CellStyle pinkStyle = createCellStyle(workbook, IndexedColors.PINK);
          CellStyle blueGreyStyle = createCellStyle(workbook, IndexedColors.BLUE_GREY);

          // 데이터 작성
          int rowIndex = 1;

          int girlCount = 0;
          int boyCount = 0;

          Map<Integer, Integer> classCountMap = new HashMap<>();
          for (Student student : classStudents) {
            Row row = sheet.createRow(rowIndex++);

            int classCount = isScience ? student.getScienceSubjects().size()
                : student.getHumanitiesSubjects().size();

            int headerCount = isScience ? sciences.size() : humanities.size();

            classCountMap.put(classCount,
                classCountMap.getOrDefault(classCount, 0) + 1);

            for (int i = 0; i < FRONT_HEADER.size(); i++) {
              Cell nextCell = row.createCell(i);

              switch (i) {
                case 0 -> nextCell.setCellValue(student.getGrade());
                case 1 -> nextCell.setCellValue(student.getClassNumber());
                case 2 -> nextCell.setCellValue(student.getStudentNumber());
                case 3 -> nextCell.setCellValue(student.getName());
                case 4 -> nextCell.setCellValue(student.getGender());
              }
              nextCell.setCellStyle(defaultStyle);

              if ("성별".equals(FRONT_HEADER.get(i))) {
                if ("남".equals(student.getGender())) {
                  nextCell.setCellStyle(blueGreyStyle);
                  boyCount++;
                } else {
                  nextCell.setCellStyle(pinkStyle);
                  girlCount++;
                }
              }
            }

            if (isScience) {
              for (int i = 0; i < sciences.size(); i++) {
                Cell nextCell = row.createCell(FRONT_HEADER.size() + i);
                int currentSubject = sciences.get(i);
                if (student.getScienceSubjects().contains(currentSubject)) {
                  nextCell.setCellValue(1);
                  nextCell.setCellStyle(greenStyle);
                } else {
                  nextCell.setCellValue(0);
                  nextCell.setCellStyle(defaultStyle);
                }
              }
            } else {
              for (int i = 0; i < humanities.size(); i++) {
                Cell nextCell = row.createCell(FRONT_HEADER.size() + i);
                int currentSubject = humanities.get(i);
                if (student.getHumanitiesSubjects().contains(currentSubject)) {
                  nextCell.setCellValue(1);
                  nextCell.setCellStyle(blueStyle);
                } else {
                  nextCell.setCellValue(0);
                  nextCell.setCellStyle(defaultStyle);
                }
              }
            }

            Cell nextCell = row.createCell(FRONT_HEADER.size() + headerCount);
            nextCell.setCellValue(isScience ? student.getScienceSubjects().size()
                : student.getHumanitiesSubjects().size());
            nextCell.setCellStyle(defaultStyle);
          }

          results.add(resultMapIndex);
          results.add(girlCount);
          results.add(boyCount);

          for (int i = 1; i <= 6; i++) {
            results.add(classCountMap.getOrDefault(i, 0));
          }

          resultMapIndex++;
        }

        // 시트 생성
        Sheet sheet = workbook.createSheet("결과");
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createCellStyle(workbook, IndexedColors.YELLOW);
        for (int i = 0; i < etcHeaders.length; i++) {
          Cell cell = headerRow.createCell(i);
          cell.setCellValue(etcHeaders[i]);
          cell.setCellStyle(headerStyle);

          sheet.setColumnWidth(i, 9 * 256);
        }

        int rowIndex = 1;
        CellStyle defaultStyle = createCellStyle(workbook, IndexedColors.WHITE);
        for (Entry<Integer, List<Integer>> entry : resultMap.entrySet()) {
          Row valueRow = sheet.createRow(rowIndex++);
          List<Integer> results = entry.getValue();
          for (int i = 0; i < results.size(); i++) {
            Cell cell = valueRow.createCell(i);
            cell.setCellStyle(defaultStyle);
            cell.setCellValue(results.get(i));
          }
        }

        // 스트림으로 데이터를 출력
        workbook.write(outputStream);
      }
    };
  }

  private static String[] getHeaders(List<String> headers, List<Integer> subjects) {
    List<String> results = new ArrayList<>(FRONT_HEADER);

    for (int index : subjects) {
      results.add(headers.get(index));
    }

    results.addAll(BACK_HEADER);
    return results.toArray(new String[0]);
  }

  private static CellStyle createCellStyle(Workbook workbook, IndexedColors backgroundColor) {
    CellStyle style = workbook.createCellStyle();
    style.setFillForegroundColor(backgroundColor.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);

    Font font = workbook.createFont();
    font.setBold(true);
    font.setColor(IndexedColors.BLACK.getIndex());
    style.setFont(font);

    return style;
  }

}
