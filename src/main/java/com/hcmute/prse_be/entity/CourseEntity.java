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

    private String title;

    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private String previewVideoUrl;

    private Double previewVideoDuration;

    private String language;

    private Double originalPrice;

    private Double averageRating = 0.0;

    private Integer totalStudents = 0;

    private Integer totalViews = 0;

    private Boolean isPublish = false;

    private Boolean isHot = false;

    private Boolean isDiscount = false;

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