package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CourseBasicDTO;
import com.hcmute.prse_be.dtos.CourseCurriculumDTO;
import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.dtos.CourseFeedbackDTO;
import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.entity.LessonProgressEntity;
import com.hcmute.prse_be.entity.VideoLessonEntity;
import com.hcmute.prse_be.response.CoursePageResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CourseService {
    List<CourseDTO> getFreeCourse(Integer page, Integer size);

    List<CourseDTO> getDiscountCourse(Integer page, Integer size);

    CoursePageResponse getCoursesBySubCategory(String keyword, Integer page);

    CoursePageResponse searchCoursesWithFilters(
            String keyword,
            Integer page,
            String price,
            Integer rating,
            String sort
    );


    CourseBasicDTO getDetailCourse(Long id, Authentication authentication);

    Page<CourseFeedbackDTO> getCourseFeedbacks(Long courseId, int page, int size);

    CourseCurriculumDTO getCourseCurriculum(Long courseId, Authentication authentication);

    boolean checkCourseAccess(Long courseId, Authentication authentication);

    VideoLessonEntity getVideoLesson(Long courseId, Long chapterId, Long lessonId);

    LessonProgressEntity getLessonProgress(Long chapterId, Long lessonId);

    void saveLessonProgress(LessonProgressEntity lessonProgress);
}
