package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CourseCurriculumDTO {
    private Double courseProgress;
    private String courseStatus;
    private Long totalLessons;
    private Long completedLessons;
    private Long remainingLessons;
    private List<ChapterDTO> chapters;
}
