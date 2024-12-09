package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
    boolean existsByStudentIdAndCourseIdAndIsActiveTrue(Long studentId, Long courseId);
    EnrollmentEntity findByStudentIdAndCourseIdAndIsActiveTrue(Long studentId, Long courseId);

    @Query("SELECT AVG(e.rating) FROM EnrollmentEntity e WHERE e.courseId = :courseId AND e.isRating = true")
    Double calculateAverageRatingByCourseId(@Param("courseId") Long courseId);
}
