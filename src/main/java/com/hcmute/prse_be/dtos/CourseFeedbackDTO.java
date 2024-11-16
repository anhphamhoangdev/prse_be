package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseFeedbackDTO {
    private Long id;

    private Long studentId;

    private String studentName;

    private String studentAvatarUrl;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}
