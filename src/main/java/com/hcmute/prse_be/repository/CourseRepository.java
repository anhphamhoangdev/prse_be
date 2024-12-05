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
import java.util.List;
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
        c.id, c.instructorId, c.title, c.shortDescription, c.description,
        c.imageUrl, c.language, c.originalPrice, cd.discountPrice, c.averageRating,
        c.totalStudents, c.totalViews, c.isPublish, c.isHot, c.isDiscount,
        c.createdAt, c.updatedAt
    )
    FROM CourseEntity c
    JOIN CourseDiscountEntity cd ON c.id = cd.courseId
    WHERE c.isPublish = true 
    AND c.originalPrice > 0
    AND cd.id IN (
        SELECT cd2.id
        FROM CourseDiscountEntity cd2
        WHERE cd2.courseId = c.id
        AND cd2.startDate <= :currentDateTime
        AND cd2.endDate >= :currentDateTime
        ORDER BY cd2.createdAt DESC
    )
""")
    Page<CourseDTO> findAllActiveDiscountedCourses(
            @Param("currentDateTime") LocalDateTime currentDateTime,
            Pageable pageable
    );

    @Query("""
        SELECT new com.hcmute.prse_be.dtos.CourseDTO(
            c.id, c.instructorId, c.title, c.shortDescription, c.description,
            c.imageUrl, c.language, c.originalPrice, c.originalPrice, c.averageRating,
            c.totalStudents, c.totalViews, c.isPublish, c.isHot, c.isDiscount,
            c.createdAt, c.updatedAt
        )
        FROM CourseEntity c
        WHERE c.isPublish = true 
        AND c.originalPrice > 0
        AND c.isHot = true
        """)
    Page<CourseDTO> findAllActiveHotCourses(
            Pageable pageable
    );

    @Query("""
    SELECT new com.hcmute.prse_be.dtos.CourseDTO(
        c.id, c.instructorId, c.title, c.shortDescription, c.description,
        c.imageUrl, c.language, c.originalPrice, cd.discountPrice, c.averageRating,
        c.totalStudents, c.totalViews, c.isPublish, c.isHot, c.isDiscount,
        c.createdAt, c.updatedAt
    )
    FROM CourseEntity c
    JOIN CourseDiscountEntity cd ON c.id = cd.courseId
    WHERE NOT EXISTS (
        SELECT 1 FROM EnrollmentEntity e
        WHERE e.courseId = c.id
        AND e.studentId = :studentId
        AND e.isActive = true
    )
    AND c.isPublish = true
    AND c.originalPrice > 0
    AND cd.id IN (
        SELECT cd2.id
        FROM CourseDiscountEntity cd2
        WHERE cd2.courseId = c.id
        AND cd2.startDate <= :currentDateTime
        AND cd2.endDate >= :currentDateTime
        ORDER BY cd2.createdAt DESC
    )
""")
    Page<CourseDTO> findAllActiveDiscountedCoursesNotEnrolled(
            @Param("studentId") Long studentId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            Pageable pageable
    );

    @Query("""
        SELECT new com.hcmute.prse_be.dtos.CourseDTO(
            c.id, c.instructorId, c.title, c.shortDescription, c.description,
            c.imageUrl, c.language, c.originalPrice, c.originalPrice, c.averageRating,
            c.totalStudents, c.totalViews, c.isPublish, c.isHot, c.isDiscount,
            c.createdAt, c.updatedAt
        )
        FROM CourseEntity c
        WHERE NOT EXISTS (
            SELECT 1 FROM EnrollmentEntity e 
            WHERE e.courseId = c.id 
            AND e.studentId = :studentId
            AND e.isActive = true
        )
        AND c.isPublish = true 
        AND c.originalPrice > 0
        AND c.isHot = true
        """)
    Page<CourseDTO> findAllActiveHotCoursesNotEnrolled(
            @Param("studentId") Long studentId,
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

    @Query("""
    SELECT NEW com.hcmute.prse_be.dtos.CourseBasicDTO(
        c.id,
        c.title,
        c.description,
        c.imageUrl,
        c.language,
        c.originalPrice,
        CASE 
            WHEN cd.discountPrice IS NOT NULL 
            AND cd.isActive = true 
            AND cd.startDate <= CURRENT_TIMESTAMP 
            AND cd.endDate >= CURRENT_TIMESTAMP 
            THEN cd.discountPrice 
            ELSE NULL 
        END,
        c.averageRating,
        c.totalStudents,
        c.totalViews,
        c.updatedAt,
        c.previewVideoUrl,
        CAST(c.previewVideoDuration AS Integer),
        c.instructorId
    )
    FROM CourseEntity c
    LEFT JOIN CourseDiscountEntity cd ON c.id = cd.courseId
    AND cd.isActive = true
    AND cd.startDate <= CURRENT_TIMESTAMP
    AND cd.endDate >= CURRENT_TIMESTAMP
    AND cd.id = (
        SELECT MAX(cd2.id)
        FROM CourseDiscountEntity cd2
        WHERE cd2.courseId = c.id
        AND cd2.isActive = true
        AND cd2.startDate <= CURRENT_TIMESTAMP
        AND cd2.endDate >= CURRENT_TIMESTAMP
    )
    WHERE c.id = :courseId
""")
    CourseBasicDTO findCourseBasicById(@Param("courseId") Long courseId);

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
        JOIN EnrollmentEntity e ON c.id = e.courseId
        WHERE e.studentId = :studentId
        AND e.isActive = true
        """)
    Page<CourseDTO> findAllMyCourses(@Param("studentId") Long studentId, Pageable pageable);

    List<CourseEntity> findAllByInstructorId(Long id);

    // calculate the number of courses registered in the current month
    @Query("SELECT COUNT(s) FROM CourseEntity s WHERE YEAR(s.createdAt) = :year AND MONTH(s.createdAt) = :month")
    long countByYearAndMonth(@Param("year") int year, @Param("month") int month);
}