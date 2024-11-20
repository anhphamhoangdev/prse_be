package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<LessonEntity, Long> {
    List<LessonEntity> findByChapterIdAndIsPublishTrue(Long chapterId);
}
