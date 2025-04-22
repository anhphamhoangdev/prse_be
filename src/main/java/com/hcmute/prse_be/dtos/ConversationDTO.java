package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long id;
    private String participantName; // Student name for instructors, instructor name for students
    private String avatarUrl; // Avatar URL of the participant
    private String latestMessage; // Latest message content
    private String latestTimestamp; // Timestamp of latest message
}