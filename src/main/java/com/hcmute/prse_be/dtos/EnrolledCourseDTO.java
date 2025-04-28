package com.hcmute.prse_be.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrolledCourseDTO {
    private Long courseId;
    private String title;
    private String imageUrl;
    private LocalDateTime enrolledAt;
    private Double progressPercent;
    private Boolean isActive;
    private String status;
    private boolean isRating;
    private Double ratingStart;
}