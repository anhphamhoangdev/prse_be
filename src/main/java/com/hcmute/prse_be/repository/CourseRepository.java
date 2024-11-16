package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.CourseBasicDTO;
import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

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
            WHERE c.isPublish = true 
            AND c.originalPrice = 0
            """)
    Page<CourseDTO> findAllByIsPublishTrueAndOriginalPrice(Pageable pageable);


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
        FROM CourseEntity c 
        WHERE c.isPublish = true 
        AND LOWER(c.title) LIKE LOWER(CONCAT('%', :q, '%')) 
        OR LOWER(c.description) LIKE LOWER(CONCAT('%', :q, '%'))
        """)
    Page<CourseDTO> findCoursesByKeyword(
            @Param("q") String q,
            Pageable pageable
    );


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
        FROM CourseEntity c 
        WHERE c.isPublish = true 
        AND (
            LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (:price = 'all' 
            OR (:price = 'free' AND c.originalPrice = 0)
            OR (:price = 'paid' AND c.originalPrice > 0)
            OR (:price = 'under_50' AND c.originalPrice < 50000)
            OR (:price = '50_200' AND c.originalPrice BETWEEN 50000 AND 200000)
            OR (:price = 'over_200' AND c.originalPrice > 200000)
        )
        AND (:rating IS NULL OR c.averageRating >= :rating)
        """)
    Page<CourseDTO> searchCoursesWithFilters(
            @Param("keyword") String keyword,
            @Param("price") String price,
            @Param("rating") Integer rating,
            Pageable pageable
    );

    // course_basic_detail
//    Optional<CourseBasicDTO> findCourseBasicById(Long courseId, Long studentId);

}