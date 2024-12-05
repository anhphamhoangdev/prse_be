package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    StudentEntity findByUsername(String username);
    StudentEntity findByEmail(String email);
    StudentEntity findByPhoneNumber(String phone);

    // calculate the number of students registered in the current month
    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE YEAR(s.createdAt) = :year AND MONTH(s.createdAt) = :month")
    long countByYearAndMonth(@Param("year") int year, @Param("month") int month);


    @Query("SELECT s FROM StudentEntity s WHERE " +
            "(:search IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR s.isActive = :status) AND " +
            "(:role IS NULL OR s.isInstructor = :role)")
    Page<StudentEntity> findAllWithFilters(
            @Param("search") String search,
            @Param("status") Boolean status,
            @Param("role") Boolean role,
            Pageable pageable
    );
}
