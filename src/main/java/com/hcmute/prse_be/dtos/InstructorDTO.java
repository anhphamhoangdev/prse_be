package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class InstructorDTO {
    private Long id;

    private String fullName;

    private String avatarUrl;

    private String title;

    private Integer totalCourses;

    private Integer totalStudents;

}
