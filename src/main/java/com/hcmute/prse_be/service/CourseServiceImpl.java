package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.Constant;
import com.hcmute.prse_be.constants.LessonType;
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
import java.util.ArrayList;
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
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final VideoLessonRepository videoLessonRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, CourseDiscountRepository courseDiscountRepository, InstructorRepository instructorRepository, CourseObjectiveRepository courseObjectiveRepository, SubCategoryRepository subCategoryRepository, CoursePrerequisiteRepository coursePrerequisiteRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, CourseFeedbackRepository courseFeedbackRepository, ChapterRepository chapterRepository, LessonRepository lessonRepository, ChapterProgressRepository chapterProgressRepository, LessonProgressRepository lessonProgressRepository, VideoLessonRepository videoLessonRepository) {
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
            List<LessonEntity> lessonOfChapter =lessonRepository.findByChapterIdAndIsPublishTrue(chapterEntity.getId());
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
            if(chapterProgress != null) {
                chapterProgressDTO.setStatus(chapterProgress.getStatus());
                chapterProgressDTO.setCompletedAt(chapterProgress.getCompletedAt());
                chapterProgressDTO.setProgressPercent(chapterProgress.getProgressPercent());

                // set progress cho lesson
                List<LessonDTO> lessons = chapterDTO.getLessons();
                lessons.forEach(lessonDTO -> {
                    LessonProgressEntity lessonProgress = lessonProgressRepository.findByLessonIdAndChapterProgressId(lessonDTO.getId(), chapterProgress.getId());
                    LessonProgressDTO lessonProgressDTO = new LessonProgressDTO();
                    if(lessonProgress != null) {
                        lessonProgressDTO.setStatus(lessonProgress.getStatus());
                        lessonProgressDTO.setCompletedAt(lessonProgress.getCompletedAt());
                        lessonProgressDTO.setLastAccessedAt(lessonProgress.getLastAccessedAt());
                    }
                    lessonDTO.setProgress(lessonProgressDTO);
                });
            }
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
    public LessonProgressEntity getLessonProgress(Long chapterId, Long lessonId) {
        return lessonProgressRepository.findByLessonIdAndChapterProgressId(lessonId, chapterId);
    }

    @Override
    public void saveLessonProgress(LessonProgressEntity lessonProgress) {
        lessonProgressRepository.save(lessonProgress);
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
