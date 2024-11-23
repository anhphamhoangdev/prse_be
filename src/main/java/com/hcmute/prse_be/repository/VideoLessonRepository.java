package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.VideoLessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoLessonRepository extends JpaRepository<VideoLessonEntity, Long> {

    VideoLessonEntity findByLessonId(Long lessonId);

    @Query("""
    SELECT vl FROM VideoLessonEntity vl
    JOIN LessonEntity l ON vl.lessonId = l.id
    JOIN ChapterEntity c ON l.chapterId = c.id
    JOIN CourseEntity co ON c.courseId = co.id
    WHERE co.id = :courseId 
    AND c.id = :chapterId 
    AND l.id = :lessonId 
    AND l.type = 'video'
""")
    VideoLessonEntity findVideoLessonByCourseAndChapterAndLesson(
            @Param("courseId") Long courseId,
            @Param("chapterId") Long chapterId,
            @Param("lessonId") Long lessonId
    );

}
