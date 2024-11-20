package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CourseCurriculumDTO {
    private List<ChapterDTO> chapters;
}
