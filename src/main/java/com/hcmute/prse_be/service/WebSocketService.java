package com.hcmute.prse_be.service;

public interface WebSocketService {
    void sendToInstructor(Long instructorId, String path,Object message);

    void testNotification(Long instructorId);
}
