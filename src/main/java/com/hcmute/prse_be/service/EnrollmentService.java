package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.EnrollmentStatsDto;
import com.hcmute.prse_be.entity.StudentEntity;

public interface EnrollmentService {
    EnrollmentStatsDto getEnrollmentStats(StudentEntity student);
}
