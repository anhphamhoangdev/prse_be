package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "submitted_code", columnDefinition = "TEXT", nullable = false)
    private String submittedCode;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "score", nullable = false)
    private Integer score = 0;

    @Column(name = "status", nullable = false)
    private String status; // PASSED, FAILED, PENDING

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "execution_time")
    private Long executionTime; // milliseconds

    @Column(name = "memory_used")
    private Long memoryUsed; // KB

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.submittedAt == null) {
            this.submittedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}