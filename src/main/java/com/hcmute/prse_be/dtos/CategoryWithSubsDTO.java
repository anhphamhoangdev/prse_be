package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryWithSubsDTO {
    private Long id;
    private String name;
    private boolean isActive;
    private int orderIndex;
    private List<SubCategoryDTO> subCategories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
