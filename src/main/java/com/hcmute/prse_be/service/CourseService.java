package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.entity.CourseEntity;

import java.util.List;

public interface CourseService {
    List<CourseEntity> getFreeCourse(Integer page, Integer size);

    List<CourseDTO> getDiscountCourse(Integer page, Integer size);

}
