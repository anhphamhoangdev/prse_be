package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.StudentCourseViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentCourseViewRepository extends JpaRepository<StudentCourseViewEntity, Long> {

    List<StudentCourseViewEntity> findTop10ByOrderByViewedAtDesc();

    @Query(value = "SELECT scv.course_id " +
            "FROM student_course_view scv " +
            "WHERE scv.student_id = :studentId " +
            "GROUP BY scv.course_id " +
            "ORDER BY MAX(scv.viewed_at) DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<Long> findTop10DistinctCourseIdsByStudentId(@Param("studentId") Long studentId);

}