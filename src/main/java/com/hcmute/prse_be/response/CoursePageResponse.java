package com.hcmute.prse_be.response;

import com.hcmute.prse_be.dtos.CourseDTO;
import lombok.Data;

import java.util.List;

@Data
public class CoursePageResponse {
    private List<CourseDTO> courses;
    private int totalPages;
    private long totalSize;

    public CoursePageResponse(List<CourseDTO> courses, int totalPages, long totalSize) {
        this.courses = courses;
        this.totalPages = totalPages;
        this.totalSize = totalSize;
    }
}
