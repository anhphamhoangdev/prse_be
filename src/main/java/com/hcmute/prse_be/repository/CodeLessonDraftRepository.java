package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.CodeLessonDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeLessonDraftRepository extends JpaRepository<CodeLessonDraftEntity, Long> {

    Optional<CodeLessonDraftEntity> findByLessonDraftId(Long lessonDraftId);
}
