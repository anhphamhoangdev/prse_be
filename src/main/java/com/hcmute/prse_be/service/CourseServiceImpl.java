package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.PaginationNumber;
import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;



    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<CourseEntity> getFreeCourse(Integer page, Integer size) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.HOME_COURSE_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return courseRepository.findAllByIsPublishTrueAndOriginalPrice(0.0, pageable);
    }


    @Override
    public List<CourseDTO> getDiscountCourse(Integer page, Integer size) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.HOME_COURSE_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return courseRepository.findAllActiveDiscountedCourses(LocalDateTime.now(),pageable).getContent();
    }


}
