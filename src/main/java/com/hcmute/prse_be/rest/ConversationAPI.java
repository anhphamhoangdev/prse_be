package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.dtos.ChatMessageDTO;
import com.hcmute.prse_be.dtos.ConversationDTO;
import com.hcmute.prse_be.dtos.WebSocketMessage;
import com.hcmute.prse_be.entity.ChatMessageEntity;
import com.hcmute.prse_be.entity.ConversationEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.CreateConversationRequest;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.*;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationAPI {

    private final StudentService studentService;

    private final InstructorService instructorService;

    private final ConversationService conversationService;

    private final ChatMessageService chatMessageService;

    private final WebSocketService webSocketService;

    public ConversationAPI(StudentService studentService, InstructorService instructorService, ConversationService conversationService, ChatMessageService chatMessageService, WebSocketService webSocketService) {
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.conversationService = conversationService;
        this.chatMessageService = chatMessageService;
        this.webSocketService = webSocketService;
    }

    @GetMapping("/instructor")
    public ResponseEntity<JSONObject> getInstructorConversations(Authentication authentication) {
        LogService.getgI().info("[ConversationAPI] getInstructorConversations username: " + authentication.getName());

        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            List<ConversationDTO> conversations = conversationService.getConversationsByInstructorId(instructor.getId());
            JSONObject response = new JSONObject();
            response.put("conversations", conversations);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Đã xảy ra lỗi khi lấy danh sách cuộc trò chuyện"));
        }
    }

    @GetMapping("/student")
    public ResponseEntity<JSONObject> getStudentConversations(Authentication authentication) {
        LogService.getgI().info("[ConversationAPI] getStudentConversations username: " + authentication.getName());

        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            List<ConversationDTO> conversations = conversationService.getConversationsByStudentId(student.getId());
            JSONObject response = new JSONObject();
            response.put("conversations", conversations);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Đã xảy ra lỗi khi lấy danh sách cuộc trò chuyện"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<JSONObject> createConversation(
            @RequestBody CreateConversationRequest request,
            Authentication authentication
    ) {
        LogService.getgI().info("[ConversationAPI] createConversation studentId: " + request.getStudentId() +
                ", instructorId: " + request.getInstructorId() +
                ", username: " + authentication.getName());

        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Validate student ID
            if (!student.getId().equals(request.getStudentId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền tạo cuộc trò chuyện"));
            }

            InstructorEntity instructor = instructorService.getInstructorById(request.getInstructorId());
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin giảng viên"));
            }

            // Check if conversation already exists
            ConversationEntity existingConversation = conversationService.findByStudentIdAndInstructorId(
                    request.getStudentId(), request.getInstructorId()
            );
            if (existingConversation != null) {
                JSONObject response = new JSONObject();
                response.put("conversationId", existingConversation.getId());
                return ResponseEntity.ok(Response.success(response));
            }

            // Create new conversation
            ConversationEntity conversation = new ConversationEntity();
            conversation.setStudentId(request.getStudentId());
            conversation.setInstructorId(request.getInstructorId());
            conversation.setCreatedAt(LocalDateTime.now());
            ConversationEntity savedConversation = conversationService.save(conversation);

            // Notify both student and instructor
            WebSocketMessage notification = WebSocketMessage.info("Bạn có cuộc trò chuyện mới!", null);
            webSocketService.sendToStudent(request.getStudentId(), "/notifications", notification);
            webSocketService.sendToInstructor(request.getInstructorId(), "/notifications", notification);

            JSONObject response = new JSONObject();
            response.put("conversationId", savedConversation.getId());
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Đã xảy ra lỗi khi tạo cuộc trò chuyện"));
        }
    }

    @GetMapping("/chat/{conversationId}/messages")
    public ResponseEntity<JSONObject> getMessages(@PathVariable Long conversationId, Authentication authentication) {
        LogService.getgI().info("[ConversationAPI] getMessages conversationId: " + conversationId + ", username: " + authentication.getName());

        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Fetch both studentId and instructorId (if any)
            Long studentId = student.getId();

            InstructorEntity instructor = instructorService.getInstructorByStudentId(studentId);

            Long instructorId = null;

            if(instructor != null) {
                instructorId = instructor.getId();
            }

            // Verify access using both IDs
            boolean hasAccess = conversationService.hasAccessToConversation(studentId, instructorId, conversationId);
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập cuộc trò chuyện"));
            }

            List<ChatMessageDTO> messageDTOs = chatMessageService.getMessagesByConversationId(conversationId);

            JSONObject response = new JSONObject();
            response.put("messages", messageDTOs);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Đã xảy ra lỗi khi lấy tin nhắn"));
        }
    }

}
