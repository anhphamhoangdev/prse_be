package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.QuizHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizHistoryRepository extends JpaRepository<QuizHistoryEntity, Long> {

    List<QuizHistoryEntity> findByStudentIdAndLessonIdOrderByIdDesc(Long studentId, Long lessonId);
}
