package com.ym.classroomassignment;

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

  public static StreamingResponseBody getExcelOutputStream(
      Map<Integer, List<Student>> groupedByClass) {
    return outputStream -> {
      try (Workbook workbook = new XSSFWorkbook()) {
        Map<Integer, List<Integer>> resultMap = new HashMap<>();

        int resultMapIndex = 1;
        for (Entry<Integer, List<Student>> ignored : groupedByClass.entrySet()) {
          resultMap.put(resultMapIndex++, new ArrayList<>());
        }

        resultMapIndex = 1;
        // 각 반별로 시트 생성 및 데이터 추가
        for (Entry<Integer, List<Student>> entry : groupedByClass.entrySet()) {
          List<Integer> results = resultMap.get(resultMapIndex);

          Integer classNumber = entry.getKey();
          List<Student> classStudents = entry.getValue();

          // 시트 생성
          Sheet sheet = workbook.createSheet(classNumber + " 반");

          CellStyle headerStyle = createCellStyle(workbook, IndexedColors.YELLOW);

          // 헤더 작성
          Row headerRow = sheet.createRow(0);
          String[] headers = {"회원코드", "학년", "반", "번호", "성별", "세계지리", "세계사", "경제", "사회·문화", "윤리와 사상",
              "사회문제 탐구", "여행지리", "물리학Ⅱ", "화학Ⅱ", "생명과학Ⅱ", "지구과학Ⅱ", "융합과학", "인문계 과목 수", "자연계 과목 수",
              "선택 과목 수"};
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
          int class1Count = 0;
          int class2Count = 0;
          int class3Count = 0;
          int class4Count = 0;
          int class5Count = 0;
          int class6Count = 0;

          for (Student student : classStudents) {
            Row row = sheet.createRow(rowIndex++);

            switch (student.classCount) {
              case 1 -> class1Count++;
              case 2 -> class2Count++;
              case 3 -> class3Count++;
              case 4 -> class4Count++;
              case 5 -> class5Count++;
              case 6 -> class6Count++;
            }

            Cell cell1 = row.createCell(0);
            cell1.setCellValue(student.getId());
            cell1.setCellStyle(defaultStyle);

            Cell cell2 = row.createCell(1);
            cell2.setCellValue(student.getGrade());
            cell2.setCellStyle(defaultStyle);

            Cell cell3 = row.createCell(2);
            cell3.setCellValue(student.getClazz());
            cell3.setCellStyle(defaultStyle);

            Cell cell4 = row.createCell(3);
            cell4.setCellValue(student.getNumber());
            cell4.setCellStyle(defaultStyle);

            Cell cell5 = row.createCell(4);
            cell5.setCellValue(student.getGender());
            if ("남".equals(student.getGender())) {
              cell5.setCellStyle(blueGreyStyle);
              boyCount++;
            } else {
              cell5.setCellStyle(pinkStyle);
              girlCount++;
            }

            Cell cell6 = row.createCell(5);
            cell6.setCellValue(student.getHumanities1());
            cell6.setCellStyle(defaultStyle);
            if (student.getHumanities1() == 1) {
              cell6.setCellStyle(blueStyle);
            }

            Cell cell7 = row.createCell(6);
            cell7.setCellValue(student.getHumanities2());
            cell7.setCellStyle(defaultStyle);
            if (student.getHumanities2() == 1) {
              cell7.setCellStyle(blueStyle);
            }

            Cell cell8 = row.createCell(7);
            cell8.setCellValue(student.getHumanities3());
            cell8.setCellStyle(defaultStyle);
            if (student.getHumanities3() == 1) {
              cell8.setCellStyle(blueStyle);
            }

            Cell cell9 = row.createCell(8);
            cell9.setCellValue(student.getHumanities4());
            cell9.setCellStyle(defaultStyle);
            if (student.getHumanities4() == 1) {
              cell9.setCellStyle(blueStyle);
            }

            Cell cell10 = row.createCell(9);
            cell10.setCellValue(student.getHumanities5());
            cell10.setCellStyle(defaultStyle);
            if (student.getHumanities5() == 1) {
              cell10.setCellStyle(blueStyle);
            }

            Cell cell11 = row.createCell(10);
            cell11.setCellValue(student.getHumanities6());
            cell11.setCellStyle(defaultStyle);
            if (student.getHumanities6() == 1) {
              cell11.setCellStyle(blueStyle);
            }

            Cell cell12 = row.createCell(11);
            cell12.setCellValue(student.getHumanities7());
            cell12.setCellStyle(defaultStyle);
            if (student.getHumanities7() == 1) {
              cell12.setCellStyle(blueStyle);
            }

            Cell cell13 = row.createCell(12);
            cell13.setCellValue(student.getNatural1());
            cell13.setCellStyle(defaultStyle);
            if (student.getNatural1() == 1) {
              cell13.setCellStyle(greenStyle);
            }

            Cell cell14 = row.createCell(13);
            cell14.setCellValue(student.getNatural2());
            cell14.setCellStyle(defaultStyle);
            if (student.getNatural2() == 1) {
              cell14.setCellStyle(greenStyle);
            }

            Cell cell15 = row.createCell(14);
            cell15.setCellValue(student.getNatural3());
            cell15.setCellStyle(defaultStyle);
            if (student.getNatural3() == 1) {
              cell15.setCellStyle(greenStyle);
            }

            Cell cell16 = row.createCell(15);
            cell16.setCellValue(student.getNatural4());
            cell16.setCellStyle(defaultStyle);
            if (student.getNatural4() == 1) {
              cell16.setCellStyle(greenStyle);
            }

            Cell cell17 = row.createCell(16);
            cell17.setCellValue(student.getNatural5());
            cell17.setCellStyle(defaultStyle);
            if (student.getNatural5() == 1) {
              cell17.setCellStyle(greenStyle);
            }

            Cell cell18 = row.createCell(17);
            cell18.setCellValue(student.getHumanitiesCount());
            cell18.setCellStyle(defaultStyle);

            Cell cell19 = row.createCell(18);
            cell19.setCellValue(student.getNaturalCount());
            cell19.setCellStyle(defaultStyle);

            Cell cell20 = row.createCell(19);
            cell20.setCellValue(student.getClassCount());
            cell20.setCellStyle(defaultStyle);
          }

          results.add(resultMapIndex);
          results.add(girlCount);
          results.add(boyCount);
          results.add(class1Count);
          results.add(class2Count);
          results.add(class3Count);
          results.add(class4Count);
          results.add(class5Count);
          results.add(class6Count);

          resultMapIndex++;
        }

        // 시트 생성
        Sheet sheet = workbook.createSheet("결과");
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createCellStyle(workbook, IndexedColors.YELLOW);
        String[] headers = {"반", "여학생 수", "남학생 수", "1과목 선택 수", "2과목 선택 수", "3과목 선택 수", "4과목 선택 수",
            "5과목 선택 수", "6과목 선택 수"};
        for (int i = 0; i < headers.length; i++) {
          Cell cell = headerRow.createCell(i);
          cell.setCellValue(headers[i]);
          cell.setCellStyle(headerStyle);

          sheet.setColumnWidth(i, 9 * 256);
        }

        int rowIndex = 1;
        CellStyle defaultStyle = createCellStyle(workbook, IndexedColors.WHITE);
        for (Entry<Integer, List<Integer>> entry : resultMap.entrySet()) {
          Row valueRow = sheet.createRow(rowIndex++);
          List<Integer> results = entry.getValue();
          for (int i = 0; i < headers.length; i++) {
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

  private static void createTwoCell(Sheet sheet, int rowIndex, CellStyle cellStyle, String value1,
      int value2) {
    Row girlRow = sheet.createRow(rowIndex);
    Cell girlCell1 = girlRow.createCell(0);
    girlCell1.setCellStyle(cellStyle);
    girlCell1.setCellValue(value1);
    Cell girlCell2 = girlRow.createCell(1);
    girlCell2.setCellStyle(cellStyle);
    girlCell2.setCellValue(value2);
  }

}
