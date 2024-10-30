package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sub_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // parent
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    private Integer orderIndex;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}