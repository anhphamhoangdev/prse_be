package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class CourseBasicDTO {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String language;
    private Double originalPrice;
    private Double discountPrice;
    private Double averageRating;
    private Integer totalStudents;
    private Integer totalViews;
    private Long totalDuration;
    private String lastUpdated;
    private String previewVideoUrl;
    private Integer previewVideoDuration;
    private boolean isEnrolled;

    private InstructorDTO instructor;
    private List<SubCategoryDTO> subcategories;
    private List<CourseObjectiveDTO> learningPoints;
    private List<CoursePrerequisiteDTO> prerequisites;


    public CourseBasicDTO(
            Long id, String title, String description, String imageUrl,
            String language, Double originalPrice, Double currentPrice,
            Double averageRating, Integer totalStudents, Integer totalViews,
            Long totalDuration, String lastUpdated, String previewVideoUrl,
            Integer previewVideoDuration, boolean isEnrolled,
            InstructorDTO instructor, SubCategoryDTO subcategories,
            CourseObjectiveDTO learningPoints, CoursePrerequisiteDTO prerequisites
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.language = language;
        this.originalPrice = originalPrice;
        this.discountPrice = currentPrice;
        this.averageRating = averageRating;
        this.totalStudents = totalStudents;
        this.totalViews = totalViews;
        this.totalDuration = totalDuration;
        this.lastUpdated = lastUpdated;
        this.previewVideoUrl = previewVideoUrl;
        this.previewVideoDuration = previewVideoDuration;
        this.isEnrolled = isEnrolled;
        this.instructor = instructor;
        this.subcategories = Collections.singletonList(subcategories);
        this.learningPoints = Collections.singletonList(learningPoints);
        this.prerequisites = Collections.singletonList(prerequisites);
    }
}
