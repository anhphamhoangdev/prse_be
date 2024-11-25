package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDTO {

    private Long id;
    private Long instructorId;
    private String title;
    private String shortDescription;
    private String description;
    private String imageUrl;
    private String language;
    private Double originalPrice;
    private Double discountPrice;
    private Double averageRating;
    private Integer totalStudents;
    private Integer totalViews;
    private Boolean isPublish;
    private Boolean isHot;
    private Boolean isDiscount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public CourseDTO(
            Long id,                // Long
            Long instructorId,      // Long
            String title,          // String
            String shortDescription, // String
            String description,    // String
            String imageUrl,       // String
            String language,       // String
            Double originalPrice,  // Double
            Double discountPrice, // Double - thay đổi từ int sang Double
            Double averageRating,  // Double
            Integer totalStudents, // Integer
            Integer totalViews,    // Integer
            Boolean isPublish,     // Boolean
            Boolean isHot,         // Boolean
            Boolean isDiscount,    // Boolean
            LocalDateTime createdAt, // LocalDateTime
            LocalDateTime updatedAt  // LocalDateTime
    ) {
        this.id = id;
        this.instructorId = instructorId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.description = description;
        this.imageUrl = imageUrl;
        this.language = language;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.averageRating = averageRating;
        this.totalStudents = totalStudents;
        this.totalViews = totalViews;
        this.isPublish = isPublish;
        this.isHot = isHot;
        this.isDiscount = isDiscount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}