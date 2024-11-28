package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecentEnrollmentDTO {
    private String studentName;
    private String studentAvatar;
    private String courseName;
    private LocalDateTime enrolledAt;
}
