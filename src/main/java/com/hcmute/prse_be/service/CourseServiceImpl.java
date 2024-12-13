package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.Constant;
import com.hcmute.prse_be.constants.LessonType;
import com.hcmute.prse_be.constants.PaginationNumber;
import com.hcmute.prse_be.dtos.*;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.repository.*;
import com.hcmute.prse_be.request.CourseFormDataRequest;
import com.hcmute.prse_be.response.CoursePageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final VideoLessonRepository videoLessonRepository;
    private final CourseSubCategoryRepository courseSubCategoryRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, CourseDiscountRepository courseDiscountRepository, InstructorRepository instructorRepository, CourseObjectiveRepository courseObjectiveRepository, SubCategoryRepository subCategoryRepository, CoursePrerequisiteRepository coursePrerequisiteRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, CourseFeedbackRepository courseFeedbackRepository, ChapterRepository chapterRepository, LessonRepository lessonRepository, ChapterProgressRepository chapterProgressRepository, LessonProgressRepository lessonProgressRepository, VideoLessonRepository videoLessonRepository, CourseSubCategoryRepository courseSubCategoryRepository) {
        this.courseRepository = courseRepository;
        this.courseDiscountRepository = courseDiscountRepository;
        this.instructorRepository = instructorRepository;
        this.courseObjectiveRepository = courseObjectiveRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.coursePrerequisiteRepository = coursePrerequisiteRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseFeedbackRepository = courseFeedbackRepository;
        this.chapterRepository = chapterRepository;
        this.lessonRepository = lessonRepository;
        this.chapterProgressRepository = chapterProgressRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.videoLessonRepository = videoLessonRepository;
        this.courseSubCategoryRepository = courseSubCategoryRepository;
    }

    @Override
    public List<CourseDTO> getFreeCourse(Integer page, Integer size) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.HOME_COURSE_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return courseRepository.findAllByIsPublishTrueAndOriginalPrice(pageable).getContent();
    }

    @Override
    public Page<CourseDTO> getMyCourse(StudentEntity studentEntity, Integer page, Integer size) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.COURSE_SUB_CATEGORY_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        return courseRepository.findAllMyCourses(studentEntity.getId() ,pageable);
    }


    @Override
    public Page<CourseDTO> getDiscountCourse(Integer page, Integer size, Authentication authentication) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.HOME_COURSE_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        LocalDateTime now = LocalDateTime.now();

        Page<CourseDTO> courseDTOs;
        if(authentication == null || !authentication.isAuthenticated()) {
            courseDTOs = courseRepository.findAllActiveDiscountedCourses(now ,pageable);
        } else {
            StudentEntity student = studentRepository.findByUsername(authentication.getName());
            courseDTOs = courseRepository.findAllActiveDiscountedCoursesNotEnrolled(student.getId(), now, pageable);
        }

        // Update discount prices
        List<CourseDTO> updatedContent = courseDTOs.getContent().stream()
                .peek(course -> courseDiscountRepository.findLatestValidDiscount(course.getId(), now).ifPresent(discount -> course.setDiscountPrice(discount.getDiscountPrice())))
                .collect(Collectors.toList());

        return new PageImpl<>(updatedContent, pageable, courseDTOs.getTotalElements());
    }

    @Override
    public Page<CourseDTO> getHotCourses(Integer page, Integer size, Authentication authentication) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.HOME_COURSE_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        LocalDateTime now = LocalDateTime.now();

        Page<CourseDTO> courseDTOs;
        if(authentication == null || !authentication.isAuthenticated()) {
            courseDTOs = courseRepository.findAllActiveHotCourses(pageable);
        } else {
            StudentEntity student = studentRepository.findByUsername(authentication.getName());
            courseDTOs = courseRepository.findAllActiveHotCoursesNotEnrolled(student.getId(), pageable);
        }

        // Update discount prices
        List<CourseDTO> updatedContent = courseDTOs.getContent().stream()
                .peek(course -> courseDiscountRepository.findLatestValidDiscount(course.getId(), now).ifPresent(discount -> course.setDiscountPrice(discount.getDiscountPrice())))
                .collect(Collectors.toList());

        return new PageImpl<>(updatedContent, pageable, courseDTOs.getTotalElements());
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
    public CourseEntity getCourse(Long courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    @Override
    public CourseBasicDTO getDetailCourse(Long id, Authentication authentication) {


        CourseEntity course = courseRepository.findById(id).orElse(null);
        if(course == null) {
            return null;
        }

        course.setTotalViews(course.getTotalViews() + 1);
        courseRepository.save(course);

        // kiem khoa hoc va set 1 vai thong tin
        CourseBasicDTO courseBasic = courseRepository.findCourseBasicById(id);

        // khong ton tai return null
        if(courseBasic == null)
        {
            return null;
        }

        // kiem discount ton tai
        CourseDiscountEntity courseDiscountEntity =  courseDiscountRepository.findLatestValidDiscount(id, LocalDateTime.now()).orElse(null);
        if(courseDiscountEntity != null) {
            courseBasic.setDiscountPrice(courseDiscountEntity.getDiscountPrice());
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

    @Override
    public List<CourseFeedbackDTO> getAllCourseFeedbacks(Long courseId) {
        List<CourseFeedbackEntity> feedbacks = courseFeedbackRepository.findAllVisibleFeedbacks(courseId);
        if (feedbacks != null) {
            return feedbacks.stream()
                    .map(CourseFeedbackDTO::convertToDTO)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public CourseCurriculumDTO getCourseCurriculum(Long courseId, Authentication authentication) {

        boolean isAuthentication = authentication != null && authentication.isAuthenticated();

        // tao ra list ChapterDTO de chua response
        List<ChapterDTO> chapters = new ArrayList<>();

        // find list chapter cua course
        List<ChapterEntity> chapterOfCourse = chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId);

        // duyet qua tung chapter => lay ra tat ca lesson cua chapter
        for (ChapterEntity chapterEntity : chapterOfCourse) {
            ChapterDTO chapterDTO = new ChapterDTO();
            chapterDTO.setId(chapterEntity.getId());
            chapterDTO.setTitle(chapterEntity.getTitle());
            // get all lesson by chapter id
            List<LessonEntity> lessonOfChapter =lessonRepository.findByChapterIdAndIsPublishTrueOrderByOrderIndexAsc(chapterEntity.getId());
            List<LessonDTO> lessons = new ArrayList<>();
            for (LessonEntity lessonEntity : lessonOfChapter) {
                LessonDTO lessonDTO = new LessonDTO();
                lessonDTO.setId(lessonEntity.getId());
                lessonDTO.setTitle(lessonEntity.getTitle());
                lessonDTO.setType(lessonEntity.getType());
                // set duration
                if(lessonEntity.getType().equals(LessonType.VIDEO)) {
//                    lessonDTO.setDuration(lessonEntity.getDuration());
                    // join qua bang video de lay ra duration
                    VideoLessonEntity video = videoLessonRepository.findByLessonId(lessonEntity.getId());
                    if(video != null) {
                        lessonDTO.setDuration(video.getDuration());
                    }
                }
                lessons.add(lessonDTO);
            }

            chapterDTO.setLessons(lessons);

//            chapterDTO.setProgress(chapterEntity.getProgress());
            chapters.add(chapterDTO);
        }

        if(authentication == null || !authentication.isAuthenticated()) {
            CourseCurriculumDTO courseCurriculumDTO = new CourseCurriculumDTO();
            courseCurriculumDTO.setChapters(chapters);
            return courseCurriculumDTO;
        }

        String username = authentication.getName();
        StudentEntity student = studentRepository.findByUsername(username);
        if(student == null) {
            CourseCurriculumDTO courseCurriculumDTO = new CourseCurriculumDTO();
            courseCurriculumDTO.setChapters(chapters);
            return courseCurriculumDTO;
        }

        // find chapter progress of student
        chapters.forEach(chapterDTO -> {
            // find chapter progress of student
            ChapterProgressEntity chapterProgress = chapterProgressRepository.findByChapterIdAndStudentId(chapterDTO.getId() ,student.getId());
            ChapterProgressDTO chapterProgressDTO = new ChapterProgressDTO();
//            chapterProgress = new ChapterProgressEntity();
//            chapterProgressDTO.setStatus(chapterProgress.getStatus());
//            chapterProgressDTO.setCompletedAt(chapterProgress.getCompletedAt());
//            chapterProgressDTO.setProgressPercent(chapterProgress.getProgressPercent());

            // set progress cho lesson
            List<LessonDTO> lessons = chapterDTO.getLessons();
            lessons.forEach(lessonDTO -> {
                LessonProgressEntity lessonProgress = lessonProgressRepository.findByLessonIdAndStudentId(lessonDTO.getId(), student.getId());
                LessonProgressDTO lessonProgressDTO = new LessonProgressDTO();
                if(lessonProgress != null) {
                    lessonProgressDTO.setStatus(lessonProgress.getStatus());
                    lessonProgressDTO.setCompletedAt(lessonProgress.getCompletedAt());
                    lessonProgressDTO.setLastAccessedAt(lessonProgress.getLastAccessedAt());
                }
                lessonDTO.setProgress(lessonProgressDTO);
            });
            chapterDTO.setProgress(chapterProgressDTO);
        });


        // find lesson progress of student
        CourseCurriculumDTO courseCurriculumDTO = new CourseCurriculumDTO();
        courseCurriculumDTO.setChapters(chapters);
        return courseCurriculumDTO;
    }

    @Override
    public boolean checkCourseAccess(Long courseId, Authentication authentication) {
        if(authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        StudentEntity student = studentRepository.findByUsername(username);
        if(student == null) {
            return false;
        }

        return enrollmentRepository.existsByStudentIdAndCourseIdAndIsActiveTrue(student.getId(), courseId);
    }

    @Override
    public VideoLessonEntity getVideoLesson(Long courseId, Long chapterId, Long lessonId) {
        return videoLessonRepository.findVideoLessonByCourseAndChapterAndLesson(
                courseId, chapterId, lessonId
        );
    }

    @Override
    public LessonProgressEntity getLessonProgress(Long studentId, Long lessonId) {
        return lessonProgressRepository.findByLessonIdAndStudentId(lessonId, studentId);
    }

    @Override
    public void saveLessonProgress(LessonProgressEntity lessonProgress) {
        lessonProgressRepository.save(lessonProgress);
    }

    @Override
    public CourseEntity createCourse(CourseFormDataRequest courseFormData, InstructorEntity instructor) {
        CourseEntity course = new CourseEntity();
        course.setTitle(courseFormData.getTitle());
        course.setDescription(courseFormData.getDescription());
        course.setShortDescription(courseFormData.getShortDescription());
        course.setLanguage(courseFormData.getLanguage());
        course.setOriginalPrice(courseFormData.getOriginalPrice());
        course.setIsDiscount(courseFormData.getIsDiscount());
        course.setIsHot(courseFormData.getIsHot());
        course.setIsPublish(courseFormData.getIsPublish());
        course.setPreviewVideoDuration(courseFormData.getPreviewVideoDuration());
        course.setInstructorId(instructor.getId());
        course = courseRepository.save(course);

        for (Long subCategoryId : courseFormData.getSubCategoryIds()) {
            SubCategoryEntity subCategory = subCategoryRepository.findById(subCategoryId).orElse(null);
            if (subCategory != null) {
                CourseSubCategoryEntity courseSubCategory = new CourseSubCategoryEntity();
                courseSubCategory.setCourseId(course.getId());
                courseSubCategory.setSubCategoryId(subCategoryId);
                courseSubCategoryRepository.save(courseSubCategory);
            }
        }

        for (String objective : courseFormData.getObjectives()) {
            CourseObjectiveEntity courseObjective = new CourseObjectiveEntity();
            courseObjective.setCourseId(course.getId());
            courseObjective.setObjective(objective);
            courseObjectiveRepository.save(courseObjective);
        }

        for (String prerequisite : courseFormData.getPrerequisites()) {
            CoursePrerequisiteEntity coursePrerequisite = new CoursePrerequisiteEntity();
            coursePrerequisite.setCourseId(course.getId());
            coursePrerequisite.setPrerequisite(prerequisite);
            coursePrerequisiteRepository.save(coursePrerequisite);
        }

        return course;
    }

    @Override
    public CourseEntity saveCourse(CourseEntity course) {
        return courseRepository.save(course);
    }

    @Override
    public List<CourseEntity> getCoursesByInstructorId(Long id) {

        return courseRepository.findAllByInstructorId(id);
    }

    @Override
    public List<ChapterEntity> getChaptersByCourseId(Long courseId) {
        return chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }

    @Override
    public List<LessonEntity> getLessonsByChapterId(Long chapterId) {
        return lessonRepository.findByChapterIdAndIsPublishTrueOrderByOrderIndexAsc(chapterId);
    }

    @Override
    public List<LessonEntity> getAllLessonByChapterId(Long chapterId) {
        return lessonRepository.findByChapterIdOrderByOrderIndexAsc(chapterId);
    }

    @Override
    public ChapterEntity getChapterById(Long chapterId) {
        return chapterRepository.findById(chapterId).orElse(null);
    }

    @Override
    public LessonEntity saveLesson(LessonEntity lessonEntity) {
        return lessonRepository.save(lessonEntity);
    }

    @Override
    public VideoLessonEntity saveVideoLesson(VideoLessonEntity videoLessonEntity) {
        LogService.getgI().info("saveVideoLesson starting...");
        return videoLessonRepository.save(videoLessonEntity);
    }

    @Override
    public ChapterEntity saveChapter(ChapterEntity chapterEntity) {
        return chapterRepository.save(chapterEntity);
    }

    @Override
    public long getCountCourse() {
        return courseRepository.count();
    }

    @Override
    public long countByYearAndMonth(int currentYear, int currentMonth) {
        return courseRepository.countByYearAndMonth(currentYear, currentMonth);
    }

    @Override
    public EnrollmentEntity findEnrollmentByStudentAndCourse(StudentEntity student, CourseEntity course) {
        return enrollmentRepository.findByStudentIdAndCourseIdAndIsActiveTrue(student.getId(), course.getId());
    }

    @Override
    public void saveEnrollment(EnrollmentEntity enrollment) {
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void saveFeedback(CourseFeedbackEntity feedback) {
        courseFeedbackRepository.save(feedback);
    }

    @Override
    public void updateCourseAverageRating(CourseEntity course) {
            // Lấy trung bình rating từ tất cả enrollment có rating
            Double averageRating = enrollmentRepository.calculateAverageRatingByCourseId(course.getId());
            course.setAverageRating(averageRating);
            courseRepository.save(course);
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
