package com.ym.classroomassignment;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
2. 학급은 1반~8반까지 있으며, 인문계 반과 자연계 반으로 구분할 예정임.
한 학급에 평균 28명이 들어가되, 인문 자연계의 학생 비율에 따라 인원이 달라질 수 있음.

3. 인문계 / 자연계
사탐 2과목 이상 선택자는 인문계
과학 2과목 이상 선택자는 자연계
(둘 모두 속하지 않는 학생은 예체능 학생으로 인문, 자연에 골고루 넣을 생각임)

4. 인문계 반에 사탐 5과목 선택자, 4과목 선택자 3과목 선택자, 2과목 선택자가 골고루 들어가도록 넣기
자연계 반에 과탐4과목 선택자 3과목 선택자 2과목 선택자가 골고루 들어가도록 넣기
예체능으로 구분한 학생이 사탐과목을 듣는 경우 인문계 반에 골고루 넣고, 과탐과목을 듣는 경우 자연계 반에 골고루 넣기
 */

@Slf4j
@Service
public class ClassAssignmentService {

  public Map<String, List<Student>> assignClass(MultipartFile file, int totalClassCount)
      throws IOException {
    // 받은 엑셀 파일 컨버팅
    List<Student> students = ExcelUtils.mapExcelToStudents(file);

    // 반 나누기 -> 인문계 / 자연계 나누기 (몇과목 선택자인지도 저장)
    // 예체능은 선택 과목에 따라 인문계 자연계에 넣기
    List<Student> humanitiesStudents = new ArrayList<>();
    List<Student> naturalStudents = new ArrayList<>();

    for (Student student : students) {
      if (student.isHumanities()) {
        humanitiesStudents.add(student);
      } else {
        naturalStudents.add(student);
      }
    }

    log.info("총 인문계: {}", humanitiesStudents.size());
    log.info("총 자연계: {}", naturalStudents.size());
    int totalStudents = humanitiesStudents.size() + naturalStudents.size();

    int humanitiesClassCount = (int) Math.round(
        (double) totalClassCount * humanitiesStudents.size() / totalStudents);
    int naturalClassCount = totalClassCount - humanitiesClassCount;

    log.info("인문계 반: {}", humanitiesClassCount);
    log.info("자연계 반: {}", naturalClassCount);

    // 반 배정
    Map<String, List<Student>> humanitiesClassAssignments = assignStudentsToClasses(
        humanitiesStudents, humanitiesClassCount, "인문계");
    Map<String, List<Student>> naturalClassAssignments = assignStudentsToClasses(naturalStudents,
        naturalClassCount, "자연계");

    Map<String, List<Student>> mergedMap = new HashMap<>(humanitiesClassAssignments);

    naturalClassAssignments.forEach((key, value) ->
        mergedMap.merge(key, value, (list1, list2) -> {
          list1.addAll(list2);
          return list1;
        })
    );

    return mergedMap;
  }


  private Map<String, List<Student>> assignStudentsToClasses(List<Student> students,
      int totalClasses, String classify) {
    // 반 별로 학생을 저장할 맵
    Map<String, List<Student>> classAssignments = new HashMap<>();
    for (int i = 1; i <= totalClasses; i++) {
      classAssignments.put(classify + " " + i, new ArrayList<>());
    }

    // 학생들을 grade, gender, classCount 기준으로 그룹화
    Map<String, List<Student>> groupedStudents = students.stream()
        .collect(Collectors.groupingBy(student ->
            student.grade() + "-" + student.gender() + "-" + student.classCount()));

    // 그룹별로 균등하게 배정
    int classIndex = 1;
    for (List<Student> group : groupedStudents.values()) {
      Collections.shuffle(group); // 랜덤 섞기
      for (Student student : group) {
        classAssignments.get(classify + " " + classIndex).add(student);
        classIndex = (classIndex % totalClasses) + 1; // 순환 배정
      }
    }

    return classAssignments;
  }

}
