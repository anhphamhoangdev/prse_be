package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_lessons_draft")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoLessonDraftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lesson_draft_id", nullable = false)
    private Long lessonDraftId;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "duration", nullable = false)
    private Double duration;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "responseFromAI", columnDefinition = "TEXT")
    private String responseFromAI;

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
