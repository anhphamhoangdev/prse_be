package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.ChatMessageDTO;

public interface WebSocketService {
    void sendToInstructor(Long instructorId, String path,Object message);

    void sendToStudent(Long studentId, String path, Object message);

    void sendChatMessage(Long conversationId, ChatMessageDTO message);

    void testNotification(Long instructorId);

    void sendMessageUpdate(Long userId, String userType, Long conversationId, String senderName, String content, String timestamp);
}
