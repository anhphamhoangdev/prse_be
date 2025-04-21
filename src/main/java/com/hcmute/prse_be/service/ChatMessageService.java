package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.ChatMessageDTO;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessageDTO> getMessagesByConversationId(Long conversationId);

    ChatMessageDTO saveMessage(Long conversationId, String senderType, Long senderId, String content);
}
