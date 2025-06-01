package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.UserSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubmissionRepository extends JpaRepository<UserSubmissionEntity, Long> {

    /**
     * Tìm submission mới nhất của student cho lesson
     */
    Optional<UserSubmissionEntity> findTopByStudentIdAndLessonIdOrderBySubmittedAtDesc(
            @Param("studentId") Long studentId,
            @Param("lessonId") Long lessonId
    );

    Optional<UserSubmissionEntity> findTopByStudentIdAndLessonIdAndStatusOrderBySubmittedAtDesc(
            @Param("studentId") Long studentId,
            @Param("lessonId") Long lessonId,
            @Param("status") String status
    );
}