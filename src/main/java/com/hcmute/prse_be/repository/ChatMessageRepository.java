package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByConversationId(Long conversationId);
    @Query(value = "SELECT cm.sender_id " +
            "FROM chat_messages cm " +
            "WHERE cm.conversation_id = :conversationId " +
            "ORDER BY cm.created_at DESC LIMIT 1", nativeQuery = true)
    Long findLatestMessageSenderId(@Param("conversationId") Long conversationId);
}