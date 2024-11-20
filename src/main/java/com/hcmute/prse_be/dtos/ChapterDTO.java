package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ChapterDTO {
    private Long id;

    private String title;

    private List<LessonDTO> lessons;

    private ChapterProgressDTO progress;
}
