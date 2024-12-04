package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class CreateChapterRequest {
    private String title;
    private Integer orderIndex;
}
