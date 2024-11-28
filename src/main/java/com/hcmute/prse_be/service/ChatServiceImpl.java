package com.hcmute.prse_be.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.request.ChatRequest;
import com.hcmute.prse_be.request.GeminiRequest;
import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ChatServiceImpl implements ChatService{

    private final String geminiApiBaseUrl = Config.getParam("gemini", "base_url");
    private final String geminiApiKey = Config.getParam("gemini", "api_key");
    private final String geminiApiUrl = geminiApiBaseUrl + "?key="+geminiApiKey;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;


    public ChatServiceImpl(ObjectMapper objectMapper, WebClient webClient) {
        this.objectMapper = objectMapper;
        this.webClient = webClient;
    }

    @Override
    public String generateContent(String message) {
        try {
            // Create GeminiRequest từ message
            GeminiRequest geminiRequest = new GeminiRequest(message);

            // Convert to JSON string
            String requestJson = objectMapper.writeValueAsString(geminiRequest);

            // Log request
            LogService.getgI().info("[generateContent] Request: " + requestJson);

            // Call API và chờ response dưới dạng JSONObject
            String responseStr = webClient.post()
                    .uri(geminiApiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse response string thành JSONObject
            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            JSONObject responseJson = (JSONObject) parser.parse(responseStr);

            String extractedText = extractTextFromResponse(responseJson);

            // Log extracted text
            LogService.getgI().info("[generateContent] Extracted text: " + extractedText);

            return extractedText;

        } catch (Exception e) {
            LogService.getgI().info("[generateContent] Error: " + e.getMessage());
            return "Error: " + e.getMessage();  // Return String instead of ResponseEntity
        }
    }

    private String extractTextFromResponse(JSONObject response) {
        try {
            // Navigate through JSON structure to get text
            JSONArray candidates = (JSONArray) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                JSONObject firstCandidate = (JSONObject) candidates.get(0);
                JSONObject content = (JSONObject) firstCandidate.get("content");
                JSONArray parts = (JSONArray) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    JSONObject firstPart = (JSONObject) parts.get(0);
                    return (String) firstPart.get("text");
                }
            }
            return "No content found in response";
        } catch (Exception e) {
            LogService.getgI().info("[extractTextFromResponse] Error extracting text: " + e.getMessage());
            return "Error extracting text from response";
        }

    }

}
