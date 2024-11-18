package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
    boolean existsByStudentIdAndCourseIdAndIsActiveTrue(Long studentId, Long courseId);
}
