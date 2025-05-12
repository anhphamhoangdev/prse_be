package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AdminLessonDTO {
    private Long id;
    private String title;
    private String type;
    private Integer orderIndex;
    private Boolean isPublish;
    private AdminVideoLessonDTO videoLesson; // For video lessons
    private List<AdminQuestionDTO> questions; // For quiz lessons
}
