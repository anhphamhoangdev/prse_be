package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubCategoryDTO {
    private Long id;
    private String name;
    private boolean isActive;
    private int orderIndex;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SubCategoryDTO()
    {}

    public SubCategoryDTO(
            Long id,
            String name,
            boolean isActive,
            int orderIndex,
            Long categoryId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.orderIndex = orderIndex;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
