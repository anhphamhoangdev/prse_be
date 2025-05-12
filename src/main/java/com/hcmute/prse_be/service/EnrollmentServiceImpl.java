package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.EnrollmentStatsDto;
import com.hcmute.prse_be.dtos.EnrollmentWithStudentDTO;
import com.hcmute.prse_be.entity.EnrollmentEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<EnrollmentWithStudentDTO> findAllEnrollmentsWithStudentByCourseId(Long courseId) {
        return enrollmentRepository.findAllEnrollmentsWithStudentByCourseId(courseId);
    }

    @Override
    public EnrollmentEntity getEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElse(null);
    }

    @Override
    public EnrollmentEntity saveEnrollment(EnrollmentEntity enrollmentEntity) {
        return enrollmentRepository.save(enrollmentEntity);
    }

}
