package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.CodeLessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CodeLessonRepository extends JpaRepository<CodeLessonEntity, Long> {
    @Query("""
   SELECT cl FROM CodeLessonEntity cl
   JOIN LessonEntity l ON cl.lessonId = l.id
   JOIN ChapterEntity c ON l.chapterId = c.id
   JOIN CourseEntity co ON c.courseId = co.id
   WHERE co.id = :courseId 
   AND c.id = :chapterId 
   AND l.id = :lessonId 
   AND l.type = 'code'
""")
    CodeLessonEntity findCodeLessonByCourseAndChapterAndLesson(
            @Param("courseId") Long courseId,
            @Param("chapterId") Long chapterId,
            @Param("lessonId") Long lessonId
    );
}
