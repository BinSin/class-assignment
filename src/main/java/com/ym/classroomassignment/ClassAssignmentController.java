package com.ym.classroomassignment;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
public class ClassAssignmentController {

  private final ClassAssignmentService classAssignmentService;

  @PostMapping(value = "/test")
  public ResponseEntity<StreamingResponseBody> assignClass(@RequestParam MultipartFile file,
      @RequestParam int totalClassCount)
      throws IOException {
    Map<String, List<Student>> groupedByClass = classAssignmentService.assignClass(file,
        totalClassCount);

    // StreamingResponseBody로 응답 생성
    StreamingResponseBody stream = outputStream -> {
      try (Workbook workbook = new XSSFWorkbook()) {
        // 각 반별로 시트 생성 및 데이터 추가
        for (Map.Entry<String, List<Student>> entry : groupedByClass.entrySet()) {
          String classNumber = entry.getKey();
          List<Student> classStudents = entry.getValue();

          // 시트 생성
          Sheet sheet = workbook.createSheet(classNumber + " 반");

          // 헤더 작성
          Row headerRow = sheet.createRow(0);
          String[] headers = {"회원코드", "학년", "반", "번호", "성별", "선택한 과목 수"};
          for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
          }

          // 데이터 작성
          int rowIndex = 1;
          for (Student student : classStudents) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(student.code());
            row.createCell(1).setCellValue(student.grade());
            row.createCell(2).setCellValue(student.clazz());
            row.createCell(3).setCellValue(student.number());
            row.createCell(4).setCellValue(student.gender());
            row.createCell(5).setCellValue(student.classCount());
          }

          // 열 크기 자동 조정
          for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
          }
        }

        // 스트림으로 데이터를 출력
        workbook.write(outputStream);
      }
    };

    String fileName = "반배정_룰렛.xlsx";
    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        .replaceAll("\\+", "%20");

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(stream);
  }

}
