package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CategoryWithSubsDTO;
import com.hcmute.prse_be.dtos.SubCategoryDTO;
import com.hcmute.prse_be.entity.CategoryEntity;
import com.hcmute.prse_be.entity.SubCategoryEntity;
import com.hcmute.prse_be.repository.CategoryRepository;
import com.hcmute.prse_be.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;


    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }


    @Override
    public List<CategoryWithSubsDTO> getAllCategoryWithSubsActive() {
        // 1. get all category active
        List<CategoryEntity> categories = categoryRepository.findAllByIsActiveTrueOrderByOrderIndexAsc();

        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. get category ids
        List<Long> categoryIds = categories.stream()
                .map(CategoryEntity::getId)
                .toList();

        // 3. get all subcategories by category_id in 1 query
        List<SubCategoryEntity> allSubCategories = subCategoryRepository.findAllActiveByCategories(categoryIds);

        // 4. Group subcategories by category_ids
        Map<Long, List<SubCategoryEntity>> subCategoriesMap = allSubCategories.stream()
                .collect(Collectors.groupingBy(SubCategoryEntity::getCategoryId));


        // 5. Map result
        return categories.stream()
                .map(category -> mapToCategoryWithSubsDTO(category, subCategoriesMap.getOrDefault(category.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    private CategoryWithSubsDTO mapToCategoryWithSubsDTO(CategoryEntity category, List<SubCategoryEntity> subCategories) {
        CategoryWithSubsDTO dto = new CategoryWithSubsDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setActive(category.getIsActive());
        dto.setOrderIndex(category.getOrderIndex());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        dto.setSubCategories(subCategories.stream()
                .map(this::mapToSubCategoryDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private SubCategoryDTO mapToSubCategoryDTO(SubCategoryEntity entity) {
        SubCategoryDTO dto = new SubCategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setActive(entity.getIsActive());
        dto.setOrderIndex(entity.getOrderIndex());
        dto.setCategoryId(entity.getCategoryId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
