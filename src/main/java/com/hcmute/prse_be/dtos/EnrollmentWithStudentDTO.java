package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EnrollmentWithStudentDTO {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long paymentLogId;
    private LocalDateTime enrolledAt;
    private String status;
    private Double progressPercent;
    private LocalDateTime completedAt;
    private Boolean isRating;
    private Double rating;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Student fields
    private Long student_id;
    private String fullName;
    private String email;
    private String avatarUrl;


    public EnrollmentWithStudentDTO(
            Long id, Long studentId, Long courseId, Long paymentLogId,
            LocalDateTime enrolledAt, String status, Double progressPercent,
            LocalDateTime completedAt, Boolean isRating, Double rating,
            Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt,
            Long student_id, String fullName, String email, String avatarUrl) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.paymentLogId = paymentLogId;
        this.enrolledAt = enrolledAt;
        this.status = status;
        this.progressPercent = progressPercent;
        this.completedAt = completedAt;
        this.isRating = isRating;
        this.rating = rating;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.student_id = student_id;
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }
}