package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.ChapterProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterProgressRepository extends JpaRepository<ChapterProgressEntity, Long>{
    ChapterProgressEntity findByChapterIdAndStudentId(Long chapterId, Long userId);

    Long countByEnrollmentIdAndStudentIdAndProgressPercent(Long enrollmentId, Long studentId, Double progressPercent);
}
