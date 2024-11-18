package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class CoursePrerequisiteDTO {

    private Long id;

    private String content;

    public CoursePrerequisiteDTO(Long id, String content)
    {
        this.id = id;
        this.content = content;
    }

}
