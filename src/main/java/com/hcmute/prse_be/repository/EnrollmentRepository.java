package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.EnrollmentStatsDto;
import com.hcmute.prse_be.dtos.EnrollmentWithStudentDTO;
import com.hcmute.prse_be.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
    boolean existsByStudentIdAndCourseIdAndIsActiveTrue(Long studentId, Long courseId);
    EnrollmentEntity findByStudentIdAndCourseIdAndIsActiveTrue(Long studentId, Long courseId);

    // New method to fetch all active enrollments for a student
    List<EnrollmentEntity> findAllByStudentIdAndIsActiveTrue(Long studentId);

    @Query("SELECT AVG(e.rating) FROM EnrollmentEntity e WHERE e.courseId = :courseId AND e.isRating = true")
    Double calculateAverageRatingByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT new com.hcmute.prse_be.dtos.EnrollmentStatsDto(" +
            "COUNT(e), " +
            "COUNT(CASE WHEN e.status = 'COMPLETED' THEN 1 END), " +
            "COUNT(CASE WHEN e.status = 'IN_PROGRESS' THEN 1 END), " +
            "COUNT(CASE WHEN e.status = 'NOT_STARTED' THEN 1 END)) " +
            "FROM EnrollmentEntity e WHERE e.studentId = :studentId")
    EnrollmentStatsDto getEnrollmentStats(@Param("studentId") Long studentId);

    @Query("SELECT new com.hcmute.prse_be.dtos.EnrollmentWithStudentDTO(" +
            "e.id, e.studentId, e.courseId, e.paymentLogId, e.enrolledAt, e.status, " +
            "e.progressPercent, e.completedAt, e.isRating, e.rating, e.isActive, " +
            "e.createdAt, e.updatedAt, s.id, s.fullName, s.email, s.avatarUrl) " +
            "FROM EnrollmentEntity e " +
            "JOIN StudentEntity s ON e.studentId = s.id " +
            "WHERE e.courseId = :courseId")
    List<EnrollmentWithStudentDTO> findAllEnrollmentsWithStudentByCourseId(@Param("courseId") Long courseId);

    // Đếm số lượng enrollment theo khóa học
    long countByCourseId(Long courseId);
}
