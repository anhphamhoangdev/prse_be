package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class StudentListDTO {
    private Long studentId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private Integer courseCount;
}