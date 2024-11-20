package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.ChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<ChapterEntity, Long> {
    List<ChapterEntity> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}
