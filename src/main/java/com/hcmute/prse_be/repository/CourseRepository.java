package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findAllByIsPublishTrueAndOriginalPrice(double originalPrice, Pageable pageable);

    @Query("""
            SELECT new com.hcmute.prse_be.dtos.CourseDTO(
                c.id,
                c.instructorId,
                c.title,
                c.shortDescription,
                c.description,
                c.imageUrl,
                c.language,
                c.originalPrice,
                cd.discountPrice,
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
            LEFT JOIN CourseDiscountEntity cd ON c.id = cd.courseId
            WHERE c.isDiscount = true 
            AND c.isPublish = true 
            AND c.originalPrice > 0
            AND cd.isActive = true
            AND cd.startDate <= :currentDateTime
            AND cd.endDate >= :currentDateTime
            """)
    Page<CourseDTO> findAllActiveDiscountedCourses(LocalDateTime currentDateTime, Pageable pageable);



}
