package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ChapterProgressDTO {

    private String status; // 'not_started', 'in_progress','completed'

    private LocalDateTime completedAt;

    private Double progressPercent;

}
