package com.hcmute.prse_be.service;

import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;

public interface ChatService {
    String generateContent(String message);
}
