package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.VideoLessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoLessonRepository extends JpaRepository<VideoLessonEntity, Long> {

    VideoLessonEntity findByLessonId(Long lessonId);

}
