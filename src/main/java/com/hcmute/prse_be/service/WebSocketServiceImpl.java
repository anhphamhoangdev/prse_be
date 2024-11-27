package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.WebSocketMessage;
import com.hcmute.prse_be.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServiceImpl implements WebSocketService{

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    public void sendToInstructor(Long instructorId, String path, Object message) {
        String destination = "/topic/instructor/" + instructorId + path; // "/topic/instructor/{instructorId}/path"
        LogService.getgI().info("Sending message to " + destination + " : " + JsonUtils.Serialize(message));
        messagingTemplate.convertAndSend(destination, message);
    }

    @Override
    public void testNotification(Long instructorId) {
        WebSocketMessage message = WebSocketMessage.info("Đăng nhập thành công!", null);
        sendToInstructor(instructorId, "/uploads",message);
    }
}
