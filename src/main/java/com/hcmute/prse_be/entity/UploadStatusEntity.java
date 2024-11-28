package com.hcmute.prse_be.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
public class UploadStatusEntity {
    private String threadId;
    private String status;
    private String title;
    private Map<String, Object> uploadResult;
    private String errorMessage;
    private LocalDateTime createdAt;
    private double progress;
    private String instructorId;

    public UploadStatusEntity(String threadId, String status) {
        this.threadId = threadId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}
