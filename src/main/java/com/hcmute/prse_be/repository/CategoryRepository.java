package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByIsActiveTrueOrderByOrderIndexAsc();

    List<CategoryEntity> findAllByOrderByOrderIndexAsc();
}
