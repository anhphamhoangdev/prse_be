package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByConversationId(Long conversationId);
}