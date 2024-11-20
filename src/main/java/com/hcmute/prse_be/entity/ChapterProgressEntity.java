package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    private Long studentId;

    @Column(nullable = false)
    private String status; //  ('not_started', 'in_progress', 'completed')

    @Column(name = "progress_percent", nullable = false)
    private Double progressPercent;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}