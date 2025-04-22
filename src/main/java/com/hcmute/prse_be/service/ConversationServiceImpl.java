package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.ConversationDTO;
import com.hcmute.prse_be.entity.ConversationEntity;
import com.hcmute.prse_be.repository.ChatMessageRepository;
import com.hcmute.prse_be.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private ChatMessageRepository chatMessageRepository; // Add repository for fetching sender_id

    @Override
    public List<ConversationDTO> getConversationsByInstructorId(Long instructorId) {
        List<Object[]> results = conversationRepository.findConversationsWithLatestMessageByInstructorId(instructorId);
        return results.stream().map(result -> {
            Long conversationId = ((Number) result[0]).longValue(); // c.id
            Long studentId = ((Number) result[2]).longValue(); // c.student_id
            String latestMessage = result[5] != null ? (String) result[5] : ""; // cm.content
            String latestTimestamp = result[6] != null ? result[6].toString() : ""; // cm.created_at
            String participantName = result[7] != null ? (String) result[7] : "Unknown Student"; // s.full_name
            String avatarUrl = result[8] != null ? (String) result[8] : null; // s.avatar_url

            // Format latestMessage: prepend "Bạn: " if sent by instructor
            Long senderId = getLatestMessageSenderId(conversationId);
            if (senderId != null && senderId.equals(instructorId)) {
                latestMessage = "Bạn: " + latestMessage;
            }

            return new ConversationDTO(
                    conversationId,
                    participantName,
                    avatarUrl,
                    latestMessage,
                    latestTimestamp
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<ConversationDTO> getConversationsByStudentId(Long studentId) {
        List<Object[]> results = conversationRepository.findConversationsWithLatestMessageByStudentId(studentId);
        return results.stream().map(result -> {
            Long conversationId = ((Number) result[0]).longValue(); // c.id
            Long instructorId = result[1] != null ? ((Number) result[1]).longValue() : null; // c.instructor_id
            String latestMessage = result[5] != null ? (String) result[5] : ""; // cm.content
            String latestTimestamp = result[6] != null ? result[6].toString() : ""; // cm.created_at
            String participantName = result[7] != null ? (String) result[7] : "Unknown Instructor"; // i.full_name
            String avatarUrl = result[8] != null ? (String) result[8] : null; // i.avatar_url

            // Format latestMessage: prepend "Bạn: " if sent by student
            Long senderId = getLatestMessageSenderId(conversationId);
            if (senderId != null && senderId.equals(studentId)) {
                latestMessage = "Bạn: " + latestMessage;
            }

            return new ConversationDTO(
                    conversationId,
                    participantName,
                    avatarUrl,
                    latestMessage,
                    latestTimestamp
            );
        }).collect(Collectors.toList());
    }

    // Helper method to fetch sender_id of the latest message
    private Long getLatestMessageSenderId(Long conversationId) {
        return chatMessageRepository.findLatestMessageSenderId(conversationId);
    }

    @Override
    public boolean hasAccessToConversation(Long studentId, Long instructorId, Long conversationId) {
        System.out.println("Checking access for studentId: " + studentId + ", instructorId: " + instructorId + ", conversationId: " + conversationId);
        return conversationRepository.findById(conversationId)
                .map(conv -> {
                    boolean hasAccess = false;
                    if (studentId != null) {
                        System.out.println("Student ID: " + conv.getStudentId());
                        hasAccess = conv.getStudentId().equals(studentId);
                    }
                    if (instructorId != null) {
                        System.out.println("Instructor ID: " + conv.getInstructorId());
                        hasAccess = hasAccess || conv.getInstructorId().equals(instructorId);
                    }
                    return hasAccess;
                })
                .orElse(false);
    }

    @Override
    public ConversationEntity findByStudentIdAndInstructorId(Long studentId, Long instructorId) {
        return conversationRepository.findByStudentIdAndInstructorId(studentId, instructorId)
                .orElse(null);
    }

    @Override
    public ConversationEntity findById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElse(null);
    }

    @Override
    public ConversationEntity save(ConversationEntity conversation) {
        return conversationRepository.save(conversation);
    }
}