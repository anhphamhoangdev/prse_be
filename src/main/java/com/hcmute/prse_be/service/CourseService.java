package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.response.CoursePageResponse;

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


}
