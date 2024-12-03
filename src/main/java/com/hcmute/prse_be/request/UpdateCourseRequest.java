package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class UpdateCourseRequest {
    private CourseInfo course;
    @Data
    public static class CourseInfo {
        private String title;
        private String description;
        private String shortDescription;
        private String imageUrl;
        private String language;
        private Boolean isPublish;
        private String previewVideoUrl;
        private Double previewVideoDuration;
        private Double originalPrice;

    }
}
