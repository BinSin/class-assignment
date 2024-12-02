package com.ym.classroomassignment.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

  private String name;               // 이름
  private int grade;                 // 학년
  private int classNumber;           // 반
  private int studentNumber;         // 번호
  private String gender;             // 성별
  private List<Integer> scienceSubjects;
  private List<Integer> humanitiesSubjects;
  private boolean wasArtsClass;      // 예체능 반 여부

}
