package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CategoryWithSubsDTO;
import com.hcmute.prse_be.entity.CategoryEntity;
import com.hcmute.prse_be.entity.SubCategoryEntity;
import com.hcmute.prse_be.response.CoursePageResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CategoryService {

    SubCategoryEntity getSubCategoryById(Long id);

    List<CategoryWithSubsDTO> getAllCategoryWithSubsActive();

    CoursePageResponse getCoursesBySubCategory(Long subCategoryId, Integer page, Authentication authentication);


    CoursePageResponse getCoursesBySubCategoryWithFilters(
            Long subCategoryId,
            String keyword,
            Integer page,
            String price,
            Integer rating,
            String sort,
            Authentication authentication
    );
}
