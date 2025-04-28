package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.EnrollmentStatsDto;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentServiceImpl implements EnrollmentService{

    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public EnrollmentStatsDto getEnrollmentStats(StudentEntity student) {
        if (student != null) {
            return enrollmentRepository.getEnrollmentStats(student.getId());
        }
        return new EnrollmentStatsDto(0, 0, 0, 0);
    }

}
