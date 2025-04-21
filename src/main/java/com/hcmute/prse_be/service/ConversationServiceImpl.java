package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.ConversationDTO;
import com.hcmute.prse_be.entity.ConversationEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.repository.ConversationRepository;
import com.hcmute.prse_be.response.Response;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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



    @Override
    public List<ConversationDTO> getConversationsByInstructorId(Long instructorId) {
        List<ConversationEntity> conversations = conversationRepository.findByInstructorId(instructorId);
        return conversations.stream()
                .map(conv -> new ConversationDTO(
                        conv.getId(),
                        studentService.findById(conv.getStudentId()).getFullName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationDTO> getConversationsByStudentId(Long studentId) {
        List<ConversationEntity> conversations = conversationRepository.findByStudentId(studentId);
        return conversations.stream()
                .map(conv -> new ConversationDTO(
                        conv.getId(),
                        instructorService.getInstructorById(conv.getInstructorId()).getFullName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAccessToConversation(Long studentId, Long instructorId, Long conversationId) {
        return conversationRepository.findById(conversationId)
                .map(conv ->
                        // Allow access if studentId matches conversation's student_id
                        conv.getStudentId().equals(studentId) ||
                                // OR instructorId (if non-null) matches conversation's instructor_id
                                (conv.getInstructorId().equals(instructorId))
                )
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