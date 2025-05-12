package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.EnrollmentStatsDto;
import com.hcmute.prse_be.dtos.EnrollmentWithStudentDTO;
import com.hcmute.prse_be.entity.EnrollmentEntity;
import com.hcmute.prse_be.entity.StudentEntity;

import java.util.List;

public interface EnrollmentService {
    EnrollmentStatsDto getEnrollmentStats(StudentEntity student);

    List<EnrollmentWithStudentDTO> findAllEnrollmentsWithStudentByCourseId(Long courseId);

    EnrollmentEntity getEnrollmentById(Long enrollmentId);

    EnrollmentEntity saveEnrollment(EnrollmentEntity enrollmentEntity);
}
