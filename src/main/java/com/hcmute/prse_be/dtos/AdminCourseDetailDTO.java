package com.hcmute.prse_be.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminCourseDetailDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private String description;
    private String imageUrl;
    private String previewVideoUrl;
    private Double previewVideoDuration;
    private String language;
    private Double originalPrice;
    private Double averageRating;
    private Integer totalStudents;
    private Integer totalViews;
    private Boolean isPublish;
    private Boolean isHot;
    private Boolean isDiscount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Thông tin giảng viên
    private Long instructorId;
    private String instructorName;
    private String instructorAvatar;
    private String instructorTitle;

    // Thông tin thống kê
    private Integer totalChapters;
    private Integer totalLessons;
    private Integer totalEnrollments;

    // Đảm bảo constructor public
    public AdminCourseDetailDTO(
            Long id, String title, String shortDescription, String description, String imageUrl,
            String previewVideoUrl, Double previewVideoDuration, String language, Double originalPrice,
            Double averageRating, Integer totalStudents, Integer totalViews, Boolean isPublish,
            Boolean isHot, Boolean isDiscount, LocalDateTime createdAt, LocalDateTime updatedAt,
            Long instructorId, String instructorName, String instructorAvatar, String instructorTitle,
            Long totalChapters, Long totalLessons, Long totalEnrollments) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.description = description;
        this.imageUrl = imageUrl;
        this.previewVideoUrl = previewVideoUrl;
        this.previewVideoDuration = previewVideoDuration;
        this.language = language;
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
        this.instructorTitle = instructorTitle;
        this.totalChapters = totalChapters != null ? totalChapters.intValue() : null;
        this.totalLessons = totalLessons != null ? totalLessons.intValue() : null;
        this.totalEnrollments = totalEnrollments != null ? totalEnrollments.intValue() : null;
    }
}