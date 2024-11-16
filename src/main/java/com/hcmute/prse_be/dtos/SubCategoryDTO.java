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
}
