package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {
    private Long id;
    private CourseDTO course;
    private String status;
    private Double progressPercent;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;

    // Constructor for JPQL
    public EnrollmentDTO(
            Long id,
            CourseDTO course,
            String status,
            Double progressPercent,
            LocalDateTime enrolledAt,
            LocalDateTime completedAt
    ) {
        this.id = id;
        this.course = course;
        this.status = status;
        this.progressPercent = progressPercent;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
    }


}