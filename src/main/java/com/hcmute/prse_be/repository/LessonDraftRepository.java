package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.LessonDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonDraftRepository extends JpaRepository<LessonDraftEntity, Long> {

    @Query(value = "SELECT ld.*, " +
            "cc.title as chapter_title, " +
            "c.title as course_title, " +
            "i.full_name as instructor_name, " +
            "s.email as instructor_email " +
            "FROM lessons_draft ld " +
            "LEFT JOIN course_chapter cc ON ld.chapter_id = cc.id " +
            "LEFT JOIN course c ON cc.course_id = c.id " +
            "LEFT JOIN instructors i ON c.instructor_id = i.id " +
            "LEFT JOIN students s ON i.student_id = s.id " +
            "ORDER BY ld.created_at DESC",
            nativeQuery = true)
    List<Object[]> findAllWithInstructorInfo();

    @Query(value = "SELECT ld.*, " +
            "cc.title as chapter_title, " +
            "c.title as course_title, " +
            "i.full_name as instructor_name, " +
            "s.email as instructor_email " +
            "FROM lessons_draft ld " +
            "LEFT JOIN course_chapter cc ON ld.chapter_id = cc.id " +
            "LEFT JOIN course c ON cc.course_id = c.id " +
            "LEFT JOIN instructors i ON c.instructor_id = i.id " +
            "LEFT JOIN students s ON i.student_id = s.id " +
            "WHERE ld.status = :status " +
            "ORDER BY ld.created_at DESC",
            nativeQuery = true)
    List<Object[]> findByStatusWithInstructorInfo(@Param("status") String status);
}
