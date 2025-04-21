package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.ConversationDTO;
import com.hcmute.prse_be.entity.ConversationEntity;

import java.util.List;

public interface ConversationService {
    List<ConversationDTO> getConversationsByInstructorId(Long instructorId);
    List<ConversationDTO> getConversationsByStudentId(Long studentId);
    boolean hasAccessToConversation(Long studentId, Long instructorId, Long conversationId);

    ConversationEntity findByStudentIdAndInstructorId(Long studentId, Long instructorId);

    ConversationEntity findById(Long conversationId);

    ConversationEntity save(ConversationEntity conversation);
}