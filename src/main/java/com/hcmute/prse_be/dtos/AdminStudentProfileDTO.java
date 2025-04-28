package com.hcmute.prse_be.dtos;

import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import lombok.Data;

import java.util.List;

@Data
public class AdminStudentProfileDTO {
    StudentEntity student;
    InstructorEntity instructor;
    List<EnrolledCourseDTO> enrolledCourses;
    Double totalSpent;
}
