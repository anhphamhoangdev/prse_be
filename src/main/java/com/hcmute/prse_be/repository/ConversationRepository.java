package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    List<ConversationEntity> findByInstructorId(Long instructorId);
    List<ConversationEntity> findByStudentId(Long studentId);

    Optional<ConversationEntity> findByStudentIdAndInstructorId(Long studentId, Long instructorId);
}