package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.RecentEnrollmentDTO;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.InstructorPlatformTransactionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InstructorPlatformTransactionRepository extends JpaRepository<InstructorPlatformTransactionEntity, Long>{

    @Query(value = """
    WITH RECURSIVE months AS (
        SELECT DATE_FORMAT(:startDate, '%Y-%m-01') as date
        UNION ALL
        SELECT DATE_ADD(date, INTERVAL 1 MONTH)
        FROM months
        WHERE DATE_ADD(date, INTERVAL 1 MONTH) < DATE_ADD(DATE_FORMAT(:endDate, '%Y-%m-01'), INTERVAL 1 MONTH)
    )
    SELECT 
        DATE_FORMAT(m.date, '%b') as month,
        COALESCE(SUM(t.instructor_money), 0) as revenue
    FROM months m
    LEFT JOIN instructor_platform_transactions t ON 
        DATE_FORMAT(t.created_at, '%Y-%m') = DATE_FORMAT(m.date, '%Y-%m')
        AND t.instructor_id = :instructorId
    WHERE m.date <= DATE_FORMAT(:endDate, '%Y-%m-01')
    GROUP BY m.date, DATE_FORMAT(m.date, '%b')
    ORDER BY m.date ASC
""", nativeQuery = true)
    List<Object[]> getRevenueStatistics(
            @Param("instructorId") Long instructorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
    SELECT NEW com.hcmute.prse_be.dtos.RecentEnrollmentDTO(
        s.fullName,
        s.avatarUrl,
        c.title,
        t.createdAt
    )
    FROM InstructorPlatformTransactionEntity t
    JOIN StudentEntity s ON t.studentId = s.id
    JOIN CourseEntity c ON t.courseId = c.id
    WHERE t.instructorId = :instructorId
    ORDER BY t.createdAt DESC
    """)
    List<RecentEnrollmentDTO> findRecentEnrollments(
            @Param("instructorId") Long instructorId,
            Pageable pageable
    );

}
