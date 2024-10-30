package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long instructorId;

    @Column(nullable = false)
    private String title;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @Column(nullable = false)
    private String language;

    @Column(name = "original_price", nullable = false)
    private Double originalPrice;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "total_students")
    private Integer totalStudents = 0;

    @Column(name = "total_views")
    private Integer totalViews = 0;

    @Column(name = "is_publish")
    private Boolean isPublish = false;

    private Boolean isHot = false;

    private Boolean isDiscount = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Một số helper methods hữu ích
    public void incrementTotalViews() {
        this.totalViews++;
    }

    public void incrementTotalStudents() {
        this.totalStudents++;
    }

    public void updateAverageRating(Double newRating) {
        if (newRating != null && newRating >= 0 && newRating <= 5) {
            this.averageRating = newRating;
        }
    }

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