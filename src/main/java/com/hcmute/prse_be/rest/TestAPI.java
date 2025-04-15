package com.hcmute.prse_be.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.dtos.WebSocketMessage;
import com.hcmute.prse_be.repository.CourseRepository;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.WebSocketService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(ApiPaths.TEST_API)
public class TestAPI {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;
    @Autowired
    private PayOS payOS;

    @Autowired
    WebSocketService webSocketService;

    String certificatePath = Config.getParam("certificate","base_file");

    @PostMapping(ApiPaths.TEST_SEND_NOTIFY_INSTRUCTOR_ID)
    public ResponseEntity<String> testNotification(@PathVariable Long instructorId) {
        LogService.getgI().info("[TestAPI] testNotification to InstructorId: " + instructorId);

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
