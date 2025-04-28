package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseListDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private String imageUrl;
    private Double originalPrice;
    private Double averageRating;
    private Integer totalStudents;
    private Boolean isPublish;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}