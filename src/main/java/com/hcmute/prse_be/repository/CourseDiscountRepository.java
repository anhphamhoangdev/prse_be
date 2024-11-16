package com.hcmute.prse_be.repository;


import com.hcmute.prse_be.entity.CourseDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CourseDiscountRepository extends JpaRepository<CourseDiscountEntity, Long> {
    @Query("""
        SELECT cd FROM CourseDiscountEntity cd
        WHERE cd.courseId = :courseId
            AND cd.isActive = true
            AND cd.startDate <= :now
            AND cd.endDate >= :now
        ORDER BY cd.id DESC
        LIMIT 1
        """)
    Optional<CourseDiscountEntity> findLatestValidDiscount(@Param("courseId") Long courseId,@Param("now") LocalDateTime now);
}
