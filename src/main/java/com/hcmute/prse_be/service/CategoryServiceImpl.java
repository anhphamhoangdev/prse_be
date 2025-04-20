package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.PaginationNumber;
import com.hcmute.prse_be.dtos.CategoryWithSubsDTO;
import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.dtos.SubCategoryDTO;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.repository.*;
import com.hcmute.prse_be.response.CoursePageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    private final SubCategoryRepository subCategoryRepository;
    private final CourseSubCategoryRepository courseSubCategoryRepository;
    private final CourseDiscountRepository courseDiscountRepository;

    private final StudentRepository studentRepository;

    private final EnrollmentRepository enrollmentRepository;


    private final CourseRepository courseRepository;


    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository, CourseSubCategoryRepository courseSubCategoryRepository, CourseDiscountRepository courseDiscountRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.courseSubCategoryRepository = courseSubCategoryRepository;
        this.courseDiscountRepository = courseDiscountRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }


    @Override
    public SubCategoryEntity getSubCategoryById(Long id) {
        return subCategoryRepository.getReferenceById(id);
    }

    @Override
    public List<CategoryWithSubsDTO> getAllCategoryWithSubsActive() {
        // 1. get all category active
        List<CategoryEntity> categories = categoryRepository.findAllByIsActiveTrueOrderByOrderIndexAsc();

        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. get category ids
        List<Long> categoryIds = categories.stream()
                .map(CategoryEntity::getId)
                .toList();

        // 3. get all subcategories by category_id in 1 query
        List<SubCategoryEntity> allSubCategories = subCategoryRepository.findAllActiveByCategories(categoryIds);

        // 4. Group subcategories by category_ids
        Map<Long, List<SubCategoryEntity>> subCategoriesMap = allSubCategories.stream()
                .collect(Collectors.groupingBy(SubCategoryEntity::getCategoryId));


        // 5. Map result
        return categories.stream()
                .map(category -> mapToCategoryWithSubsDTO(category, subCategoriesMap.getOrDefault(category.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public CoursePageResponse getCoursesBySubCategory(Long subCategoryId, Integer page, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, PaginationNumber.COURSE_SUB_CATEGORY_PER_PAGE, Sort.by("createdAt").descending());

        // Get courses
        Page<CourseDTO> coursePage = courseSubCategoryRepository
                .findCoursesBySubCategory(subCategoryId, pageable);

        // Initialize filtered courses with all courses by default
        List<CourseDTO> filteredCourses = coursePage.getContent();

        // Filter for authenticated users
        if (authentication != null) {
            // Find the student
            StudentEntity student = studentRepository.findByUsername(authentication.getName());
            if (student != null) {
                // Get course IDs the student is actively enrolled in
                List<Long> enrolledCourseIds = enrollmentRepository
                        .findAllByStudentIdAndIsActiveTrue(student.getId())
                        .stream()
                        .map(EnrollmentEntity::getCourseId)
                        .toList();

                // Filter out enrolled courses
                filteredCourses = coursePage.getContent()
                        .stream()
                        .filter(course -> !enrolledCourseIds.contains(course.getId()))
                        .toList();
            }
        }

        // Process discount prices
        List<CourseDTO> processedCourses = filteredCourses.stream()
                .map(this::processDiscountPrice)
                .toList();

        return new CoursePageResponse(
                processedCourses,
                coursePage.getTotalPages(),
                coursePage.getTotalElements()
        );
    }
    @Override
    public CoursePageResponse getCoursesBySubCategoryWithFilters(
            Long subCategoryId,
            String keyword,
            Integer page,
            String price,
            Integer rating,
            String sort,
            Authentication authentication
    ) {
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
        Page<CourseDTO> coursePage = courseSubCategoryRepository
                .findCoursesBySubCategoryWithFilters(
                        subCategoryId,
                        keyword,
                        price,
                        rating,
                        pageable
                );

        // Initialize filtered courses with all courses by default
        List<CourseDTO> filteredCourses = coursePage.getContent();

        // Filter for authenticated users
        if (authentication != null) {
            // Find the student
            StudentEntity student = studentRepository.findByUsername(authentication.getName());
            if (student != null) {
                // Get course IDs the student is actively enrolled in
                List<Long> enrolledCourseIds = enrollmentRepository
                        .findAllByStudentIdAndIsActiveTrue(student.getId())
                        .stream()
                        .map(EnrollmentEntity::getCourseId)
                        .toList();

                // Filter out enrolled courses
                filteredCourses = coursePage.getContent()
                        .stream()
                        .filter(course -> !enrolledCourseIds.contains(course.getId()))
                        .toList();
            }
        }

        // Process discount prices
        List<CourseDTO> processedCourses = filteredCourses.stream()
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

    private CategoryWithSubsDTO mapToCategoryWithSubsDTO(CategoryEntity category, List<SubCategoryEntity> subCategories) {
        CategoryWithSubsDTO dto = new CategoryWithSubsDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setActive(category.getIsActive());
        dto.setOrderIndex(category.getOrderIndex());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        dto.setSubCategories(subCategories.stream()
                .map(this::mapToSubCategoryDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private SubCategoryDTO mapToSubCategoryDTO(SubCategoryEntity entity) {
        SubCategoryDTO dto = new SubCategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setActive(entity.getIsActive());
        dto.setOrderIndex(entity.getOrderIndex());
        dto.setCategoryId(entity.getCategoryId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
