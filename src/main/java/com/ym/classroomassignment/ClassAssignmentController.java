package com.ym.classroomassignment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClassAssignmentController {

  @GetMapping("/")
  public String showUploadPage() {
    return "upload";
  }

}
