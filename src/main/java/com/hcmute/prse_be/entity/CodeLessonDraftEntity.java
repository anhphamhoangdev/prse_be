package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_lessons_draft")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeLessonDraftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lesson_draft_id", nullable = false)
    private Long lessonDraftId;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "initial_code", columnDefinition = "TEXT")
    private String initialCode;

    @Column(name = "solution_code", nullable = false, columnDefinition = "TEXT")
    private String solutionCode;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(name = "hints")
    private String hints;

    @Column(name = "difficulty_level")
    private String difficultyLevel = "easy";

    // Test case fields
    @Column(name = "test_case_input", columnDefinition = "TEXT")
    private String testCaseInput;

    @Column(name = "test_case_output", columnDefinition = "TEXT")
    private String testCaseOutput;

    @Column(name = "test_case_description")
    private String testCaseDescription;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
