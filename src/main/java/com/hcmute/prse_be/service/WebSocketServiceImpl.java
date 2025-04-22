package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.ChatMessageDTO;
import com.hcmute.prse_be.dtos.WebSocketMessage;
import com.hcmute.prse_be.util.JsonUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendToInstructor(Long instructorId, String path, Object message) {
        String destination = "/topic/instructor/" + instructorId + path;
        LogService.getgI().info("Sending message to " + destination + " : " + JsonUtils.Serialize(message));
        messagingTemplate.convertAndSend(destination, message);
    }

    @Override
    public void sendToStudent(Long studentId, String path, Object message) {
        String destination = "/topic/student/" + studentId + path;
        LogService.getgI().info("Sending message to " + destination + " : " + JsonUtils.Serialize(message));
        messagingTemplate.convertAndSend(destination, message);
    }

    public void sendChatMessage(Long conversationId, ChatMessageDTO messageDTO) {
        WebSocketMessage wsMessage = new WebSocketMessage();
        wsMessage.setType("NEW_MESSAGE");
        wsMessage.setData(messageDTO);
        wsMessage.setStatus("info");
        wsMessage.setMessage("New message received");

        LogService.getgI().info("Broadcasting message to /topic/conversation/" + conversationId);
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, wsMessage);
    }

    public void sendMessageUpdate(Long userId, String userType, Long conversationId, String senderName, String content, String timestamp) {
        WebSocketMessage wsMessage = new WebSocketMessage();
        wsMessage.setType("MESSAGE_UPDATE");
        wsMessage.setData(Map.of(
                "conversationId", conversationId,
                "senderName", senderName,
                "content", content,
                "timestamp", timestamp
        ));
        wsMessage.setStatus("info");
        wsMessage.setMessage("New message update");

        String destination = userType.equals("INSTRUCTOR") ?
                "/topic/instructor/" + userId + "/messages" :
                "/topic/student/" + userId + "/messages";
        LogService.getgI().info("Sending MESSAGE_UPDATE to " + destination);
        messagingTemplate.convertAndSend(destination, wsMessage);
    }

    @Override
    public void testNotification(Long instructorId) {
        WebSocketMessage message = WebSocketMessage.info("Đăng nhập thành công!", null);
        sendToInstructor(instructorId, "/uploads", message);
    }
}