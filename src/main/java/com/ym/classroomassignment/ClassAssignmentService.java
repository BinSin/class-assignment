package com.ym.classroomassignment;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public Map<Integer, List<Student>> assignClasses(MultipartFile file, int humanitiesCount,
      int naturalCount)
      throws IOException {
    List<Student> students = ExcelUploadUtils.mapExcelToStudents(file);

    // 1. 인문계와 자연계 분리
    List<Student> humanities = students.stream().filter(Student::isHumanities).toList();
    List<Student> naturals = students.stream().filter(student -> !student.isHumanities()).toList();

    // 2. 각각 그룹 배정
    Map<Integer, List<Student>> humanitiesAssignments = assignGroupBalanced(humanities, 1,
        humanitiesCount);
    Map<Integer, List<Student>> naturalAssignments = assignGroupBalanced(naturals,
        humanitiesCount + 1, naturalCount);

    // 3. 두 그룹 합치기
    Map<Integer, List<Student>> allAssignments = new HashMap<>();
    allAssignments.putAll(humanitiesAssignments);
    allAssignments.putAll(naturalAssignments);

    return allAssignments;
  }

  private Map<Integer, List<Student>> assignGroupBalanced(List<Student> group, int startClassId,
      int numClasses) {
    // 초기화
    Map<Integer, List<Student>> classAssignments = new HashMap<>();
    for (int i = 0; i < numClasses; i++) {
      classAssignments.put(startClassId + i, new ArrayList<>());
    }

    // 1. 성별 그룹화
    List<Student> females = new ArrayList<>(group.stream().filter(Student::isFemale).toList());
    List<Student> males = new ArrayList<>(
        group.stream().filter(student -> !student.isFemale()).toList());

    // 2. 과목 선택 그룹화
    Map<Integer, List<Student>> subjectGroups = groupBySubjects(group);

    // 3. 학생 랜덤 섞기
    Collections.shuffle(females);
    Collections.shuffle(males);

    // 4. 성별 균형 배정
    assignStudentsByGender(classAssignments, females, startClassId, numClasses);
    assignStudentsByGender(classAssignments, males, startClassId, numClasses);

    // 5. 과목 선택 균형 배정
    assignStudentsBySubjects(classAssignments, subjectGroups, startClassId, numClasses);

    return classAssignments;
  }

  private Map<Integer, List<Student>> groupBySubjects(List<Student> group) {
    Map<Integer, List<Student>> subjectGroups = new HashMap<>();
    for (int subjectIndex = 0; subjectIndex < 12; subjectIndex++) {
      int index = subjectIndex;
      List<Student> studentsInSubject = new ArrayList<>(group.stream()
          .filter(student -> {
            if (index < 7) {
              return student.humanitiesSubjects.get(index) == 1;
            } else {
              return student.naturalSubjects.get(index - 7) == 1;
            }
          })
          .toList());
      subjectGroups.put(subjectIndex, studentsInSubject);
    }
    return subjectGroups;
  }

  private void assignStudentsByGender(Map<Integer, List<Student>> classAssignments,
      List<Student> students,
      int startClassId, int numClasses) {
    int currentClass = startClassId;
    for (Student student : students) {
      classAssignments.get(currentClass).add(student);
      currentClass++;
      if (currentClass >= startClassId + numClasses) {
        currentClass = startClassId;
      }
    }
  }

  private void assignStudentsBySubjects(Map<Integer, List<Student>> classAssignments,
      Map<Integer, List<Student>> subjectGroups, int startClassId, int numClasses) {
    int currentClass = startClassId;

    for (List<Student> subjectGroup : subjectGroups.values()) {
      Collections.shuffle(subjectGroup); // 과목별 그룹을 랜덤하게 섞기
      for (Student student : subjectGroup) {
        // 중복 배정을 방지
        if (!isStudentAlreadyAssigned(classAssignments, student)) {
          classAssignments.get(currentClass).add(student);
          currentClass++;
          if (currentClass >= startClassId + numClasses) {
            currentClass = startClassId;
          }
        }
      }
    }
  }

  private boolean isStudentAlreadyAssigned(Map<Integer, List<Student>> classAssignments,
      Student student) {
    return classAssignments.values().stream().anyMatch(classList -> classList.contains(student));
  }

}