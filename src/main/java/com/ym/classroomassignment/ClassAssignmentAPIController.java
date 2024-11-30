package com.ym.classroomassignment;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class ClassAssignmentAPIController {

  private final ClassAssignmentService classAssignmentService;

  @PostMapping(value = "/upload")
  public ResponseEntity<StreamingResponseBody> assignClass(@RequestParam MultipartFile file,
      @RequestParam int humanitiesCount, @RequestParam int naturalCount)
      throws IOException {
    Map<Integer, List<Student>> groupedByClass = classAssignmentService.assignClasses(file,
        humanitiesCount, naturalCount);

    // StreamingResponseBody로 응답 생성
    StreamingResponseBody stream = ExcelDownloadUtils.getExcelOutputStream(groupedByClass);

    String fileName = "반배정_룰렛.xlsx";
    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        .replaceAll("\\+", "%20");

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(stream);
  }

}
