package com.hcmute.prse_be.dtos;

import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import lombok.Data;

import java.util.List;

@Data
public class AdminInstructorProfileDTO {
    private InstructorEntity instructor;
    private StudentEntity studentAccount;
    private List<StudentListDTO> students;
    private List<CourseEntity> courses;
    private Double totalRevenue;
}