package com.ym.classroomassignment.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Value;

@Value
public class AssignClassRequest {

  List<String> headers = new ArrayList<>();
  List<List<Object>> fullData = new ArrayList<>();
  List<Integer> artsClasses = new ArrayList<>();
  List<Integer> humanities = new ArrayList<>();
  List<Integer> sciences = new ArrayList<>();
  int humanitiesCount;
  int sciencesCount;

}
