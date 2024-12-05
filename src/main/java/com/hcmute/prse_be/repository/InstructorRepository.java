package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.InstructorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InstructorRepository extends JpaRepository<InstructorEntity, Long> {
    InstructorEntity findByStudentId(Long studentId);

    // calculate the number of instructors registered in the current month
    @Query("SELECT COUNT(s) FROM InstructorEntity s WHERE YEAR(s.createdAt) = :year AND MONTH(s.createdAt) = :month")
    long countByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
