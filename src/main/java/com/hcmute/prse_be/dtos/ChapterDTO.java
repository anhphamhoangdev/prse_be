package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class ChapterDTO {
    private Long id;

    private String title;

    private LessonDTO[] lessons;
}
