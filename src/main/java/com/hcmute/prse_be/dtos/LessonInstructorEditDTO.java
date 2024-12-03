package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class LessonInstructorEditDTO {

    Long id;
    String title;
    String type;
    boolean isPublish;
    int orderIndex;
}
