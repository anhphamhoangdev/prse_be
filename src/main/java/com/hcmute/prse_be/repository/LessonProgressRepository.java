package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.LessonProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonProgressRepository extends JpaRepository<LessonProgressEntity, Long> {
    LessonProgressEntity findByLessonIdAndStudentId(Long lessonId, Long studentId);

    Long countByChapterProgressIdAndStudentId(Long chapterProgressId, Long studentId);

    @Query("SELECT COUNT(lp) FROM LessonProgressEntity lp " +
            "JOIN ChapterProgressEntity cp ON lp.chapterProgressId = cp.id " +
            "WHERE cp.enrollmentId = :enrollmentId AND lp.status = 'COMPLETED'")
    Long countCompletedByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

}
