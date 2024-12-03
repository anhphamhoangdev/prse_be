package com.hcmute.prse_be.request;

import lombok.Data;

import java.util.List;

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

    private Double previewVideoDuration;

    private List<Long> subCategoryIds;

    private List<String> prerequisites;

    private List<String> objectives;

}
