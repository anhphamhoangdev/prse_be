package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LessonProgressDTO {

    private String status; // 'not_started', 'completed'

    private LocalDateTime completedAt;

    private LocalDateTime lastAccessedAt;

}
