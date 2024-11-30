package com.ym.classroomassignment;

import java.util.List;
import lombok.Getter;

@Getter
public class Student {

  String id;
  int grade;
  int clazz;
  int number;
  String gender; // M or F
  int classCount; // 인문계 수강수와 자연계 수강수 중 큰 값
  int humanities1;
  int humanities2;
  int humanities3;
  int humanities4;
  int humanities5;
  int humanities6;
  int humanities7;
  int natural1;
  int natural2;
  int natural3;
  int natural4;
  int natural5;
  List<Integer> humanitiesSubjects;
  List<Integer> naturalSubjects;

  public Student(String id, int grade, int clazz, int number, String gender,
      List<Integer> humanitiesSubjects, List<Integer> naturalSubjects) {
    this.id = id;
    this.grade = grade;
    this.clazz = clazz;
    this.number = number;
    this.gender = gender;

    this.humanitiesSubjects = humanitiesSubjects;
    this.humanities1 = humanitiesSubjects.get(0);
    this.humanities2 = humanitiesSubjects.get(1);
    this.humanities3 = humanitiesSubjects.get(2);
    this.humanities4 = humanitiesSubjects.get(3);
    this.humanities5 = humanitiesSubjects.get(4);
    this.humanities6 = humanitiesSubjects.get(5);
    this.humanities7 = humanitiesSubjects.get(6);

    this.naturalSubjects = naturalSubjects;
    this.natural1 = naturalSubjects.get(0);
    this.natural2 = naturalSubjects.get(1);
    this.natural3 = naturalSubjects.get(2);
    this.natural4 = naturalSubjects.get(3);
    this.natural5 = naturalSubjects.get(4);

    // classCount 계산: 인문계 수강수와 자연계 수강수 중 큰 값
    this.classCount = Math.max(getHumanitiesCount(), getNaturalCount());
  }

  // 인문계 여부 확인
  public boolean isHumanities() {
    return humanitiesSubjects.stream().mapToInt(Integer::intValue).sum()
        > naturalSubjects.stream().mapToInt(Integer::intValue).sum();
  }

  public int getHumanitiesCount() {
    return humanitiesSubjects.stream().mapToInt(Integer::intValue).sum();
  }

  public int getNaturalCount() {
    return naturalSubjects.stream().mapToInt(Integer::intValue).sum();
  }

  public boolean isFemale() {
    return "여".equals(gender);
  }

  @Override
  public String toString() {
    return id + " (" + grade + "-" + gender + "-" + classCount + ")";
  }

}
