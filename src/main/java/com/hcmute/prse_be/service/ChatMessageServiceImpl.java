package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.ChatMessageDTO;
import com.hcmute.prse_be.entity.ChatMessageEntity;
import com.hcmute.prse_be.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Override
    public List<ChatMessageDTO> getMessagesByConversationId(Long conversationId) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByConversationId(conversationId);
        return messages.stream()
                .map(msg -> new ChatMessageDTO(
                        msg.getId(),
                        msg.getConversationId(),
                        msg.getSenderId(),
                        msg.getSenderType(),
                        msg.getSenderName(),
                        msg.getContent(),
                        msg.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    public ChatMessageDTO saveMessage(Long conversationId, String senderType, Long senderId, String senderName, String content) {
        ChatMessageEntity message = new ChatMessageEntity();
        message.setConversationId(conversationId);
        message.setSenderType(senderType);
        message.setSenderName(senderName);
        message.setContent(content);
        message.setSenderId(senderId);
        message.setCreatedAt(LocalDateTime.now());
        ChatMessageEntity savedMessage = chatMessageRepository.save(message);

        return new ChatMessageDTO(
                savedMessage.getId(),
                savedMessage.getConversationId(),
                savedMessage.getSenderId(),
                savedMessage.getSenderType(),
                savedMessage.getSenderName(),
                savedMessage.getContent(),
                savedMessage.getCreatedAt().toString()
        );
    }
}