package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class LessonDTO {
    private Long id;

    private String title;

    private String type;

    private Integer duration;

    private LessonProgressDTO progress;
}
