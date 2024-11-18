package com.hcmute.prse_be.dtos;

import com.hcmute.prse_be.entity.CourseFeedbackEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseFeedbackDTO {
    private Long id;

    private Long studentId;

    private String studentName;

    private String studentAvatarUrl;

    private Double rating;

    private String comment;

    private LocalDateTime createdAt;

    public static CourseFeedbackDTO convertToDTO(CourseFeedbackEntity entity) {
        CourseFeedbackDTO dto = new CourseFeedbackDTO();
        dto.setId(entity.getId());
        dto.setStudentId(entity.getStudentId());
        dto.setStudentName(entity.getStudentName());
        dto.setStudentAvatarUrl(entity.getStudentAvatarUrl());
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
