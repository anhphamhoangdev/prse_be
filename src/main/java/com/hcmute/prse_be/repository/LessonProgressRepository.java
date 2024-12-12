package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.LessonProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgressRepository extends JpaRepository<LessonProgressEntity, Long> {
    LessonProgressEntity findByLessonIdAndStudentId(Long lessonId, Long studentId);

}
