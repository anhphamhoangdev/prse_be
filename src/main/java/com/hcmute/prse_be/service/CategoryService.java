package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CategoryWithSubsDTO;
import com.hcmute.prse_be.entity.CategoryEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryWithSubsDTO> getAllCategoryWithSubsActive();

}
