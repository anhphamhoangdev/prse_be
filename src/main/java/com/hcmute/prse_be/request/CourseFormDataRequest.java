package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class CourseFormDataRequest {

    private String title;

    private String description;

    private String shortDescription;

    private String language;

    private Double originalPrice;

    private Boolean isDiscount;

    private Boolean isHot;

    private Boolean isPublish;

    private String previewVideoDuration;
}
