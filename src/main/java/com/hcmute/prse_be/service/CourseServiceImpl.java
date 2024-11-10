package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.PaginationNumber;
import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.repository.CourseDiscountRepository;
import com.hcmute.prse_be.repository.CourseRepository;
import com.hcmute.prse_be.response.CoursePageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseDiscountRepository courseDiscountRepository;


    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, CourseDiscountRepository courseDiscountRepository) {
        this.courseRepository = courseRepository;
        this.courseDiscountRepository = courseDiscountRepository;
    }

    @Override
    public List<CourseDTO> getFreeCourse(Integer page, Integer size) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.HOME_COURSE_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return courseRepository.findAllByIsPublishTrueAndOriginalPrice(pageable).getContent();
    }


    @Override
    public List<CourseDTO> getDiscountCourse(Integer page, Integer size) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.HOME_COURSE_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return courseRepository.findAllActiveDiscountedCourses(LocalDateTime.now(),pageable).getContent();
    }

    @Override
    public CoursePageResponse getCoursesBySubCategory(String keyword, Integer page) {
        Pageable pageable = PageRequest.of(page, PaginationNumber.COURSE_SUB_CATEGORY_PER_PAGE, Sort.by("id").descending());
        // Get courses
        Page<CourseDTO> coursePage = courseRepository
                .findCoursesByKeyword(keyword, pageable);

        // Process discount prices
        List<CourseDTO> processedCourses = coursePage.getContent().stream()
                .map(this::processDiscountPrice)
                .toList();

        return new CoursePageResponse(processedCourses, coursePage.getTotalPages(),coursePage.getTotalElements());
    }

    @Override
    public CoursePageResponse searchCoursesWithFilters(
            String keyword,
            Integer page,
            String price,
            Integer rating,
            String sort
    ) {
        // Tạo Pageable với sort tương ứng
        Sort sorting = switch (sort) {
            case "oldest" -> Sort.by("createdAt").ascending();
            case "price_asc" -> Sort.by("originalPrice").ascending();
            case "price_desc" -> Sort.by("originalPrice").descending();
            case "rating" -> Sort.by("averageRating").descending();
            case "popular" -> Sort.by("totalStudents").descending();
            default -> Sort.by("createdAt").descending(); // newest
        };

        Pageable pageable = PageRequest.of(
                page,
                PaginationNumber.COURSE_SUB_CATEGORY_PER_PAGE,
                sorting
        );

        // Get courses with filters
        Page<CourseDTO> coursePage = courseRepository.searchCoursesWithFilters(
                keyword,
                price,
                rating,
                pageable
        );

        // Process discount prices
        List<CourseDTO> processedCourses = coursePage.getContent().stream()
                .map(this::processDiscountPrice)
                .toList();

        return new CoursePageResponse(
                processedCourses,
                coursePage.getTotalPages(),
                coursePage.getTotalElements()
        );
    }

    private CourseDTO processDiscountPrice(CourseDTO course) {
        if (Boolean.TRUE.equals(course.getIsDiscount())) {
            courseDiscountRepository.findLatestValidDiscount(course.getId(), LocalDateTime.now())
                    .ifPresentOrElse(
                            discount -> course.setDiscountPrice(discount.getDiscountPrice()),
                            () -> {
                                CourseEntity courseEntity = courseRepository.getReferenceById(course.getId());
                                courseEntity.setIsDiscount(false);
                                courseRepository.save(courseEntity);
                                course.setDiscountPrice(course.getOriginalPrice());
                            }
                    );
        }
        return course;
    }



}
