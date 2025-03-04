package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.request.ChatRequest;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.ChatService;
import com.hcmute.prse_be.service.LogService;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.CHAT_API)
public class ChatAPI {
    private final ChatService chatService;

    public ChatAPI(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("")
    public ResponseEntity<JSONObject> generate(@RequestBody ChatRequest request) {
        LogService.getgI().info("[ChatAPI] generateGemini "+ request.getMessage());
        // Assume ChatRequest has a 'message' field
        JSONObject response = new JSONObject();
        response.put("message", chatService.generateContent(request.getMessage()));
        return ResponseEntity.ok(Response.success(response));
    }
}
