package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InstructorRepository extends JpaRepository<InstructorEntity, Long> {
    InstructorEntity findByStudentId(Long studentId);
    // calculate the number of instructors registered in the current month
    @Query("SELECT COUNT(s) FROM InstructorEntity s WHERE YEAR(s.createdAt) = :year AND MONTH(s.createdAt) = :month")
    long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // count total courses by instructor id
    @Query("SELECT COUNT(c) FROM CourseEntity c WHERE c.instructorId = :instructorId")
    long countCoursesByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(DISTINCT e.studentId) " +
            "FROM EnrollmentEntity e " +
            "JOIN CourseEntity c ON e.courseId = c.id " +
            "WHERE c.instructorId = :instructorId " +
            "AND e.isActive = true")
    long countUniqueStudentsByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT i FROM InstructorEntity i WHERE " +
            "(:search IS NULL OR LOWER(i.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR i.isActive = :status)")
    Page<InstructorEntity> findAllWithFilters(
            @Param("search") String search,
            @Param("status") Boolean status,
            Pageable pageable
    );
}
