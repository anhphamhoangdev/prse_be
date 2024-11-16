package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.SubCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategoryEntity, Long> {
    @Query("SELECT s FROM SubCategoryEntity s WHERE s.isActive = true AND s.categoryId IN :categoryIds ORDER BY s.categoryId, s.orderIndex ASC")
    List<SubCategoryEntity> findAllActiveByCategories(List<Long> categoryIds);
}
