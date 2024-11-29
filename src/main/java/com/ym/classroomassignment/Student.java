package com.ym.classroomassignment;

public record Student(String code, int grade, int clazz, int number, String gender,
                      int humanitiesCount,
                      int naturalCount) {

  public boolean isHumanities() {
    return this.humanitiesCount > this.naturalCount;
  }

  public int classCount() {
    return Math.max(humanitiesCount, naturalCount);
  }

}
