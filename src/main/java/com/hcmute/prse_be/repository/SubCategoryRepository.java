package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.SubCategoryDTO;
import com.hcmute.prse_be.entity.SubCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategoryEntity, Long> {
    @Query("SELECT s FROM SubCategoryEntity s WHERE s.isActive = true AND s.categoryId IN :categoryIds ORDER BY s.categoryId, s.orderIndex ASC")
    List<SubCategoryEntity> findAllActiveByCategories(List<Long> categoryIds);


    @Query("""
        SELECT NEW com.hcmute.prse_be.dtos.SubCategoryDTO(
            s.id,
            s.name,
            s.isActive,
            s.orderIndex,
            s.categoryId,
            s.createdAt,
            s.updatedAt
        )
        FROM SubCategoryEntity s
        JOIN CourseSubCategoryEntity cs ON s.id = cs.subCategoryId
        WHERE cs.courseId = :courseId
        AND cs.isActive = true
        AND s.isActive = true
        ORDER BY s.orderIndex ASC
    """)
    List<SubCategoryDTO> findByCourseId(@Param("courseId") Long courseId);
}
