package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.Constant;
import com.hcmute.prse_be.constants.PaginationNumber;
import com.hcmute.prse_be.dtos.*;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.repository.*;
import com.hcmute.prse_be.response.CoursePageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseDiscountRepository courseDiscountRepository;

    private final InstructorRepository instructorRepository;
    private final CourseObjectiveRepository courseObjectiveRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CoursePrerequisiteRepository coursePrerequisiteRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseFeedbackRepository courseFeedbackRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, CourseDiscountRepository courseDiscountRepository, InstructorRepository instructorRepository, CourseObjectiveRepository courseObjectiveRepository, SubCategoryRepository subCategoryRepository, CoursePrerequisiteRepository coursePrerequisiteRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, CourseFeedbackRepository courseFeedbackRepository) {
        this.courseRepository = courseRepository;
        this.courseDiscountRepository = courseDiscountRepository;
        this.instructorRepository = instructorRepository;
        this.courseObjectiveRepository = courseObjectiveRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.coursePrerequisiteRepository = coursePrerequisiteRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseFeedbackRepository = courseFeedbackRepository;
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

    @Override
    public CourseBasicDTO getDetailCourse(Long id, Authentication authentication) {

        // kiem khoa hoc va set 1 vai thong tin
        CourseBasicDTO courseBasic = courseRepository.findCourseBasicById(id);

        // khong ton tai return null
        if(courseBasic == null)
        {
            return null;
        }

        // lay ra thong tin instructor
        Optional<InstructorEntity> instructorOptional = instructorRepository.findById(courseBasic.getInstructor().getId());
        if (instructorOptional.isEmpty()) {
            courseBasic.getInstructor().setTitle("N/A");
            courseBasic.getInstructor().setAvatarUrl(Constant.DEFAULT_AVATAR_URL);
            courseBasic.getInstructor().setTotalCourses(0);
            courseBasic.getInstructor().setTotalStudents(0);
            courseBasic.getInstructor().setFullName("N/A");
        }
        else {
            InstructorEntity instructorEntity = instructorOptional.get();
            courseBasic.getInstructor().setTitle(instructorEntity.getTitle());
            courseBasic.getInstructor().setAvatarUrl(instructorEntity.getAvatarUrl());
            courseBasic.getInstructor().setTotalCourses(instructorEntity.getTotalCourse());
            courseBasic.getInstructor().setTotalStudents(instructorEntity.getTotalStudent());
            courseBasic.getInstructor().setFullName(instructorEntity.getFullName());
        }

        // lay ra objective cua khoa hoc
        List<CourseObjectiveDTO> courseObjectiveDTOS = courseObjectiveRepository.findDTOsByCourseId(id);
        courseBasic.setLearningPoints(courseObjectiveDTOS);

        // lay ra cac subcategory cua khoa hoc
        List<SubCategoryDTO> subCategoryDTOS = subCategoryRepository.findByCourseId(id);
        courseBasic.setSubcategories(subCategoryDTOS);

        // lay ra cac prerequisite cua khoa hoc
        List<CoursePrerequisiteDTO> coursePrerequisiteDTOS = coursePrerequisiteRepository.findDTOsByCourseId(id);
        courseBasic.setPrerequisites(coursePrerequisiteDTOS);


        // Kiem ra enroll cua user
        if(authentication == null || !authentication.isAuthenticated()) {
            courseBasic.setEnrolled(false);
        }
        else {
            String username = authentication.getName();
            StudentEntity student = studentRepository.findByUsername(username);
            boolean isEnrolled = false; // default
            if(student != null)
            {
                isEnrolled = enrollmentRepository.existsByStudentIdAndCourseIdAndIsActiveTrue(student.getId(), id);
            }
            courseBasic.setEnrolled(isEnrolled);
        }

        return courseBasic;
    }

    @Override
    public Page<CourseFeedbackDTO> getCourseFeedbacks(Long courseId, int page, int size) {
        Page<CourseFeedbackEntity> feedbacks = courseFeedbackRepository.findVisibleFeedbacks(
                courseId,
                PageRequest.of(page, size)
        );

        return feedbacks.map(CourseFeedbackDTO::convertToDTO);
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
