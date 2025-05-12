package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CourseWithInstructorDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private String imageUrl;
    private Double originalPrice;
    private Double averageRating;
    private Integer totalStudents;
    private Integer totalViews;
    private Boolean isPublish;
    private Boolean isHot;
    private Boolean isDiscount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long instructorId;
    private String instructorName;
    private String instructorAvatar;

    public CourseWithInstructorDTO(Long id, String title, String shortDescription, String imageUrl, Double originalPrice,
                                   Double averageRating, Integer totalStudents, Integer totalViews, Boolean isPublish,
                                   Boolean isHot, Boolean isDiscount, LocalDateTime createdAt, LocalDateTime updatedAt,
                                   Long instructorId, String instructorName, String instructorAvatar) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.originalPrice = originalPrice;
        this.averageRating = averageRating;
        this.totalStudents = totalStudents;
        this.totalViews = totalViews;
        this.isPublish = isPublish;
        this.isHot = isHot;
        this.isDiscount = isDiscount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.instructorAvatar = instructorAvatar;
    }
}
