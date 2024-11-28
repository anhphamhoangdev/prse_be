package com.hcmute.prse_be.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hcmute.prse_be.dtos.WebSocketMessage;
import com.hcmute.prse_be.repository.CourseRepository;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.WebSocketService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestAPI {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;
    @Autowired
    private PayOS payOS;

    @Autowired
    WebSocketService webSocketService;

    @PostMapping("/send-notification/{instructorId}")
    public ResponseEntity<String> testNotification(@PathVariable Long instructorId) {
        try {
            // Tạo một message test
            WebSocketMessage message = WebSocketMessage.info(
                    "Xin chào! Đây là thông báo test.", null
            );
            webSocketService.sendToInstructor(instructorId, "/uploads",message);

            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending notification: " + e.getMessage());
        }
    }




}
