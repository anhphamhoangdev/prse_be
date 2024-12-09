package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class CourseBasicDTO {

    private Long id;
    private String title;
    private String description;
    private String shortDescription;
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
            Long id,
            String title,
            String description,
            String shortDescription,
            String imageUrl,
            String language,
            Double originalPrice,
            Double discountPrice,
            Double averageRating,
            Integer totalStudents,
            Integer totalViews,
            LocalDateTime lastUpdated,
            String previewVideoUrl,
            Integer previewVideoDuration,
            Long instructorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.language = language;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.averageRating = averageRating;
        this.totalStudents = totalStudents;
        this.totalViews = totalViews;
        this.lastUpdated = lastUpdated.toString();
        this.previewVideoUrl = previewVideoUrl;
        this.previewVideoDuration = previewVideoDuration;
        this.instructor = new InstructorDTO();
        this.instructor.setId(instructorId);
    }

}
