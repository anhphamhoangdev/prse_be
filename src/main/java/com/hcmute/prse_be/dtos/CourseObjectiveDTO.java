package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class CourseObjectiveDTO {

    private Long id;

    private String content;

    public CourseObjectiveDTO(Long id, String content)
    {
        this.id = id;
        this.content = content;
    }



}
