package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.VideoLessonDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoLessonDraftRepository extends JpaRepository<VideoLessonDraftEntity, Long> {

    Optional<VideoLessonDraftEntity> findByLessonDraftId(Long draftId);
}
