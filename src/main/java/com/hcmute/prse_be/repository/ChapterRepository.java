package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.ChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<ChapterEntity, Long> {
    List<ChapterEntity> findByCourseIdAndIsPublishTrueOrderByOrderIndexAsc(Long courseId);

    Optional<ChapterEntity> findByIdAndCourseIdAndIsPublishTrue(Long chapterId, Long courseId);

    Long countByCourseIdAndIsPublishTrue(Long courseId);
}
