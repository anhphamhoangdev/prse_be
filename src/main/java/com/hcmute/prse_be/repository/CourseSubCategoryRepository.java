package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.entity.CourseSubCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSubCategoryRepository extends JpaRepository<CourseSubCategoryEntity, Long> {
    @Query(value = """
        SELECT NEW com.hcmute.prse_be.dtos.CourseDTO(
            c.id,                   
            c.instructorId,          
            c.title,                 
            c.shortDescription,     
            c.description,          
            c.imageUrl,             
            c.language,             
            c.originalPrice,         
            c.originalPrice,        
            c.averageRating,        
            c.totalStudents,        
            c.totalViews,           
            c.isPublish,            
            c.isHot,                
            c.isDiscount,           
            c.createdAt,            
            c.updatedAt             
        )
        FROM CourseSubCategoryEntity csc
        JOIN CourseEntity c ON csc.courseId = c.id
        WHERE csc.subCategoryId = :subCategoryId 
            AND csc.isActive = true 
            AND c.isPublish = true
        """)
    Page<CourseDTO> findCoursesBySubCategory(
            @Param("subCategoryId") Long subCategoryId,
            Pageable pageable
    );

    @Query("""
    SELECT NEW com.hcmute.prse_be.dtos.CourseDTO(
        c.id,
        c.instructorId,
        c.title,
        c.shortDescription,
        c.description,
        c.imageUrl,
        c.language,
        c.originalPrice,
        c.originalPrice,
        c.averageRating,
        c.totalStudents,
        c.totalViews,
        c.isPublish,
        c.isHot,
        c.isDiscount,
        c.createdAt,
        c.updatedAt
    )
    FROM CourseEntity c
    JOIN CourseSubCategoryEntity csc ON c.id = csc.courseId
    WHERE c.isPublish = true
    AND csc.subCategoryId = :subCategoryId
    AND (:keyword = '' OR 
        LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND (:price = 'all' 
        OR (:price = 'free' AND c.originalPrice = 0)
        OR (:price = 'paid' AND c.originalPrice > 0)
        OR (:price = 'under_50' AND c.originalPrice < 50000)
        OR (:price = '50_200' AND c.originalPrice BETWEEN 50000 AND 200000)
        OR (:price = 'over_200' AND c.originalPrice > 200000)
    )
    AND (:rating = 0 OR c.averageRating >= :rating)
    """)
    Page<CourseDTO> findCoursesBySubCategoryWithFilters(
            @Param("subCategoryId") Long subCategoryId,
            @Param("keyword") String keyword,
            @Param("price") String price,
            @Param("rating") Integer rating,
            Pageable pageable
    );
}
