package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LessonRepository extends JpaRepository<LessonEntity, Long> {
    List<LessonEntity> findByChapterIdAndIsPublishTrueOrderByOrderIndexAsc(Long chapterId);

    List<LessonEntity> findByChapterIdOrderByOrderIndexAsc(Long chapterId);

    Long countByChapterIdAndIsPublishTrue(Long chapterId);

    @Query("SELECT COUNT(l) FROM LessonEntity l " +
            "JOIN ChapterEntity c ON l.chapterId = c.id " +
            "WHERE c.courseId = :courseId AND l.isPublish = true")
    Long countByCourseIdAndIsPublishTrue(Long courseId);
}
