package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.LessonProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgressRepository extends JpaRepository<LessonProgressEntity, Long> {
    LessonProgressEntity findByLessonIdAndChapterProgressId(Long lessonId, Long chapterProgressId);

}
