package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ChapterInstructorEditDTO {
    Long id;
    String title;
    List<LessonInstructorEditDTO> lessons;
    int orderIndex;
}
