package com.hcmute.prse_be.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AdminChapterDTO {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Boolean isPublish;
    private List<AdminLessonDTO> lessons;
}