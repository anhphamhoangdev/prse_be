package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.dtos.ChatMessageDTO;
import com.hcmute.prse_be.entity.ConversationEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.ChatRequest;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.*;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.CHAT_API)
public class ChatAPI {
    private final ChatService chatService;
    private final StudentService studentService;
    private final InstructorService instructorService;
    private final ConversationService conversationService;
    private final ChatMessageService chatMessageService;
    private final WebSocketService webSocketService;

    public ChatAPI(ChatService chatService, StudentService studentService, InstructorService instructorService, ConversationService conversationService, ChatMessageService chatMessageService, WebSocketService webSocketService) {
        this.chatService = chatService;
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.conversationService = conversationService;
        this.chatMessageService = chatMessageService;
        this.webSocketService = webSocketService;
    }

    @PostMapping("")
    public ResponseEntity<JSONObject> generate(@RequestBody ChatRequest request) {
        LogService.getgI().info("[ChatAPI] generateGemini " + request.getMessage());
        JSONObject response = new JSONObject();
        response.put("message", chatService.generateContent(request.getMessage()));
        return ResponseEntity.ok(Response.success(response));
    }

    @MessageMapping("/send-message")
    public void sendMessage(@Payload ChatMessageDTO messageDTO) {
        LogService.getgI().info("[ChatAPI] sendMessage senderId: " + messageDTO.getSenderId() +
                " senderType: " + messageDTO.getSenderType() +
                " conversationId: " + messageDTO.getConversationId() +
                " content: " + messageDTO.getContent() +
                " username: " + messageDTO.getSenderName());

        try {
            Long senderId;
            if ("STUDENT".equals(messageDTO.getSenderType())) {
                StudentEntity student = studentService.findById(messageDTO.getSenderId());
                if (student == null) {
                    throw new IllegalArgumentException("Không tìm thấy sinh viên");
                }
                senderId = student.getId();
                if (!conversationService.hasAccessToConversation(senderId, null, messageDTO.getConversationId())) {
                    throw new IllegalArgumentException("Không có quyền truy cập cuộc trò chuyện");
                }
            } else if ("INSTRUCTOR".equals(messageDTO.getSenderType())) {
                InstructorEntity instructor = instructorService.getInstructorById(messageDTO.getSenderId());
                if (instructor == null) {
                    throw new IllegalArgumentException("Không tìm thấy giảng viên");
                }
                senderId = instructor.getId();
                if (!conversationService.hasAccessToConversation(null, senderId, messageDTO.getConversationId())) {
                    throw new IllegalArgumentException("Không có quyền truy cập cuộc trò chuyện");
                }
            } else {
                throw new IllegalArgumentException("senderType không hợp lệ");
            }

            // Save message and get DTO
            ChatMessageDTO responseDTO = chatMessageService.saveMessage(
                    messageDTO.getConversationId(),
                    messageDTO.getSenderType(),
                    senderId,
                    messageDTO.getSenderName(),
                    messageDTO.getContent()
            );

            // Notify participants
            ConversationEntity conversation = conversationService.findById(messageDTO.getConversationId());
            if (conversation == null) {
                throw new IllegalStateException("Không tìm thấy cuộc trò chuyện");
            }

            // Broadcast to conversation topic
            webSocketService.sendChatMessage(messageDTO.getConversationId(), responseDTO);

            // Send MESSAGE_UPDATE to both participants
            webSocketService.sendMessageUpdate(
                    conversation.getInstructorId(), "INSTRUCTOR",
                    messageDTO.getConversationId(), messageDTO.getSenderName(),
                    messageDTO.getContent(), responseDTO.getTimestamp()
            );
            webSocketService.sendMessageUpdate(
                    conversation.getStudentId(), "STUDENT",
                    messageDTO.getConversationId(), messageDTO.getSenderName(),
                    messageDTO.getContent(), responseDTO.getTimestamp()
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Đã xảy ra lỗi khi gửi tin nhắn: " + e.getMessage(), e);
        }
    }
}