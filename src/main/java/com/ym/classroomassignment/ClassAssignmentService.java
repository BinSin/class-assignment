package com.ym.classroomassignment;


import com.ym.classroomassignment.dto.AssignClassRequest;
import com.ym.classroomassignment.dto.Student;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/*
2. 학급은 1반~8반까지 있으며, 인문계 반과 자연계 반으로 구분할 예정임.
한 학급에 평균 28명이 들어가되, 인문 자연계의 학생 비율에 따라 인원이 달라질 수 있음.

3. 인문계 / 자연계
과학 2과목 이상 선택자는 자연계
그 외는 자연계
예체능 학생은 인문, 자연 골고루 분배

4. 과목 선택자가 골고루 들어가도록 넣기 (5과목 선택자, 4과목 선택자 3과목 선택자, 2과목 선택자가 골고루)
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassAssignmentService {

  public List<List<Student>> assignClasses(AssignClassRequest request) {
    List<List<Object>> fullData = request.getFullData().stream()
        .filter(e -> !CollectionUtils.isEmpty(e)).toList();
    List<Integer> humanitiesIndices = request.getHumanities(); // 인문계 과목 인덱스
    List<Integer> sciencesIndices = request.getSciences(); // 자연계 과목 인덱스
    List<Integer> artsClassNumbers = request.getArtsClasses(); // 예체능 반

    int humanitiesCount = request.getHumanitiesCount(); // 인문계 반 수
    int naturalCount = request.getSciencesCount(); // 자연계 반 수

    // 학생 분리
    List<Student> humanitiesStudents = new ArrayList<>();
    List<Student> sciencesStudents = new ArrayList<>();
    List<Student> artsStudents = new ArrayList<>();

    for (List<Object> row : fullData) {
      int grade = getAsInteger(row.get(0));
      int classNumber = getAsInteger(row.get(1));
      int studentNumber = getAsInteger(row.get(2));
      String name = getAsString(row.get(3));
      String gender = getAsString(row.get(4));
      boolean wasArtsClass = artsClassNumbers.contains(classNumber);

      // 과목 수 계산
      List<Integer> scienceSubjects = findSubjects(row, sciencesIndices);
      List<Integer> humanitiesSubjects = findSubjects(row, humanitiesIndices);

      if (wasArtsClass) {
        artsStudents.add(
            new Student(name, grade, classNumber, studentNumber, gender, scienceSubjects,
                humanitiesSubjects, true));
      } else if (scienceSubjects.size() >= 2) {
        sciencesStudents.add(
            new Student(name, grade, classNumber, studentNumber, gender, scienceSubjects,
                humanitiesSubjects, false));
      } else {
        humanitiesStudents.add(
            new Student(name, grade, classNumber, studentNumber, gender, scienceSubjects,
                humanitiesSubjects, false));
      }
    }

    // 인문계 학생 반 배정
    List<List<Student>> humanitiesClasses = assignBalanced(humanitiesStudents, humanitiesCount, 1,
        true);

    // 자연계 학생 반 배정
    List<List<Student>> sciencesClasses = assignBalanced(sciencesStudents, naturalCount,
        humanitiesCount + 1, false);

    // 예체능 학생 반 배정
    distributeArtsStudents(artsStudents, humanitiesClasses, sciencesClasses);

    // 결과 반환
    List<List<Student>> allClasses = new ArrayList<>();
    allClasses.addAll(humanitiesClasses);
    allClasses.addAll(sciencesClasses);

    return allClasses;
  }

  private List<List<Student>> assignBalanced(List<Student> students, int numClasses,
      int startClassNumber, boolean isHumanities) {
    // 각 반 초기화
    List<List<Student>> classes = new ArrayList<>();
    for (int i = 0; i < numClasses; i++) {
      classes.add(new ArrayList<>());
    }

    // 반별 학생 수, 과목 수, 성별별 학생 수 추적
    Map<Integer, Integer> classCounts = new HashMap<>();
    Map<Integer, Integer> subjectCounts = new HashMap<>();
    Map<Integer, Map<String, Integer>> genderCounts = new HashMap<>();

    for (int i = 0; i < numClasses; i++) {
      int classId = startClassNumber + i;
      classCounts.put(classId, 0);
      subjectCounts.put(classId, 0);
      genderCounts.put(classId, new HashMap<>());
      genderCounts.get(classId).put("M", 0); // 남학생 수
      genderCounts.get(classId).put("F", 0); // 여학생 수
    }

    // 학생들을 반-성별-과목 수 기준으로 그룹화
    Map<String, List<Student>> groupedByCriteria = students.stream()
        .collect(Collectors.groupingBy(student -> {
          int classNumber = student.getClassNumber();
          String gender = student.getGender();
          int subjectCount = isHumanities ? student.getHumanitiesSubjects().size()
              : student.getScienceSubjects().size();

          return classNumber + "-" + gender + "-" + subjectCount;
        }));

    // 그룹별 데이터를 섞기
    List<Student> shuffledStudents = new ArrayList<>();
    groupedByCriteria.values().forEach(group -> {
      Collections.shuffle(group); // 그룹 내 학생 섞기
      shuffledStudents.addAll(group);
    });

    // 학생 분배
    for (Student student : shuffledStudents) {
      int subjectCount = isHumanities
          ? student.getHumanitiesSubjects().size()
          : student.getScienceSubjects().size();
      String gender = student.getGender();
  
      // 가장 적합한 반 선택
      int targetClassIndex = findOptimalClass(classCounts, subjectCounts, genderCounts,
          startClassNumber, numClasses, subjectCount, gender);
      classes.get(targetClassIndex - startClassNumber).add(student);

      // 반별 학생 수, 과목 수, 성별 수 갱신
      classCounts.put(targetClassIndex, classCounts.get(targetClassIndex) + 1);
      subjectCounts.put(targetClassIndex, subjectCounts.get(targetClassIndex) + subjectCount);
      genderCounts.get(targetClassIndex)
          .put(gender, genderCounts.get(targetClassIndex).getOrDefault(gender, 0) + 1);
    }

    return classes;
  }

  private int findOptimalClass(Map<Integer, Integer> classCounts,
      Map<Integer, Integer> subjectCounts,
      Map<Integer, Map<String, Integer>> genderCounts, // 성별별 학생 수
      int startClassNumber, int numClasses,
      int subjectCount, String gender) {
    return classCounts.entrySet().stream()
        .filter(entry -> entry.getKey() >= startClassNumber
            && entry.getKey() < startClassNumber + numClasses)
        .min(Comparator.comparingInt(entry -> {
          int classId = entry.getKey();
          int studentCount = entry.getValue();
          int subjectSum = subjectCounts.getOrDefault(classId, 0);

          // 성별 균형 고려
          int genderCount = genderCounts.get(classId).getOrDefault(gender, 0);
          int oppositeGenderCount =
              genderCounts.get(classId).values().stream().mapToInt(Integer::intValue).sum()
                  - genderCount;

          // 점수 계산 (학생 수 우선 고려, 과목 수 및 성별 균형 추가)
          return studentCount * 1000 + Math.abs(subjectSum - subjectCount) * 100
              + Math.abs(genderCount - oppositeGenderCount) * 10;
        }))
        .map(Map.Entry::getKey)
        .orElseThrow(() -> new IllegalStateException("적절한 반을 찾을 수 없습니다."));
  }

  private void distributeArtsStudents(
      List<Student> artsStudents,
      List<List<Student>> humanitiesClasses,
      List<List<Student>> sciencesClasses
  ) {
    // 모든 반 통합
    List<List<Student>> allClasses = new ArrayList<>();
    allClasses.addAll(humanitiesClasses);
    allClasses.addAll(sciencesClasses);

    // 반별 수업 수 합계 계산
    Map<List<Student>, Integer> classSubjectCounts = new HashMap<>();
    for (List<Student> cls : allClasses) {
      int subjectSum = cls.stream()
          .mapToInt(
              student -> student.getHumanitiesSubjects().size() + student.getScienceSubjects()
                  .size())
          .sum();
      classSubjectCounts.put(cls, subjectSum);
    }

    // 예체능 학생 배정
    for (Student artsStudent : artsStudents) {
      // 학생 수와 수업 수 합계가 가장 적은 반 선택
      List<Student> targetClass = findOptimalClass(allClasses, classSubjectCounts);
      targetClass.add(artsStudent);

      // 반별 수업 수 합계 갱신
      int newSubjectCount = classSubjectCounts.getOrDefault(targetClass, 0)
          + artsStudent.getHumanitiesSubjects().size() + artsStudent.getScienceSubjects().size();
      classSubjectCounts.put(targetClass, newSubjectCount);
    }
  }

  private List<Student> findOptimalClass(
      List<List<Student>> classes,
      Map<List<Student>, Integer> classSubjectCounts
  ) {
    return classes.stream()
        .min(Comparator.comparingInt(cls -> {
          int studentCount = cls.size();
          int subjectCount = classSubjectCounts.get(cls);
          return studentCount * 100 + subjectCount; // 학생 수에 더 큰 가중치
        }))
        .orElseThrow(() -> new IllegalStateException("적절한 반을 찾을 수 없습니다."));
    }

  private List<Integer> findSubjects(List<Object> row, List<Integer> subjectIndices) {
    List<Integer> subjects = new ArrayList<>();
    for (int index : subjectIndices) {
      int value = getAsInteger(row.get(index));
      if (value == 1) {
        subjects.add(index);
      }
    }
    return subjects;
  }

  private String getAsString(Object obj) {
    if (obj == null) {
      return null;
    }
    return obj.toString();
  }

  private Integer getAsInteger(Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof Integer) {
      return (Integer) obj;
    }
    if (obj instanceof String) {
      try {
        return Integer.parseInt((String) obj);
      } catch (NumberFormatException e) {
        return null;
      }
    }
    if (obj instanceof Double) {
      return ((Double) obj).intValue();
    }
    return null;
  }

}