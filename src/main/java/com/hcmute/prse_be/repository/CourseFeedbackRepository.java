package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.CourseFeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseFeedbackRepository extends JpaRepository<CourseFeedbackEntity, Long> {

    @Query("SELECT f FROM CourseFeedbackEntity f " +
            "WHERE f.courseId = :courseId " +
            "AND f.isHidden = false " +
            "ORDER BY f.rating DESC, f.createdAt DESC")
    Page<CourseFeedbackEntity> findVisibleFeedbacks(
            @Param("courseId") Long courseId,
            Pageable pageable
    );

    @Query("SELECT f FROM CourseFeedbackEntity f " +
            "WHERE f.courseId = :courseId " +
            "AND f.isHidden = false " +
            "ORDER BY f.rating DESC, f.createdAt DESC")
    List<CourseFeedbackEntity> findAllVisibleFeedbacks(@Param("courseId") Long courseId);

    Optional<CourseFeedbackEntity> findByStudentIdAndCourseIdAndIsHiddenFalse(Long studentId, Long courseId);
}
