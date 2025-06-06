package com.hcmute.prse_be.service;

import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.constants.Constant;
import com.hcmute.prse_be.constants.LessonType;
import com.hcmute.prse_be.constants.PaginationNumber;
import com.hcmute.prse_be.constants.StatusType;
import com.hcmute.prse_be.dtos.*;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.repository.*;
import com.hcmute.prse_be.request.*;
import com.hcmute.prse_be.response.CoursePageResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private static final String RECOMMENDATION_URL = Config.getParam("ai", "recommendation_url");
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
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final StudentCourseViewRepository studentCourseViewRepository;
    private final WebClient webClient;
    private final LessonDraftRepository lessonDraftRepository;
    private final VideoLessonDraftRepository videoLessonDraftRepository;
    private final CodeLessonDraftRepository codeLessonDraftRepository;
    private final CodeLessonRepository codeLessonRepository;


    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, CourseDiscountRepository courseDiscountRepository, InstructorRepository instructorRepository, CourseObjectiveRepository courseObjectiveRepository, SubCategoryRepository subCategoryRepository, CoursePrerequisiteRepository coursePrerequisiteRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, CourseFeedbackRepository courseFeedbackRepository, ChapterRepository chapterRepository, LessonRepository lessonRepository, ChapterProgressRepository chapterProgressRepository, LessonProgressRepository lessonProgressRepository, VideoLessonRepository videoLessonRepository, CourseSubCategoryRepository courseSubCategoryRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, StudentCourseViewRepository studentCourseViewRepository, WebClient webClient, LessonDraftRepository lessonDraftRepository, VideoLessonDraftRepository videoLessonDraftRepository, CodeLessonDraftRepository codeLessonDraftRepository, CodeLessonRepository codeLessonRepository) {
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
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.studentCourseViewRepository = studentCourseViewRepository;
        this.webClient = webClient;
        this.lessonDraftRepository = lessonDraftRepository;
        this.videoLessonDraftRepository = videoLessonDraftRepository;
        this.codeLessonDraftRepository = codeLessonDraftRepository;
        this.codeLessonRepository = codeLessonRepository;
    }

    @Override
    public List<CourseDTO> getFreeCourse(Integer page, Integer size) {
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.HOME_COURSE_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return courseRepository.findAllByIsPublishTrueAndOriginalPrice(pageable).getContent();
    }

    public Page<EnrollmentDTO> getMyCourse(StudentEntity studentEntity, String status, Integer page, Integer size) {
        // Pagination setup
        int currentPage = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : PaginationNumber.COURSE_SUB_CATEGORY_PER_PAGE;
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        // if status = "all"
        Page<EnrollmentEntity> enrollmentPage;
        if (status == null || status.equals("all")) {
            enrollmentPage = courseRepository.findAllMyEnrollments(
                    studentEntity.getId(),
                    pageable
            );
        }
        else
        {
            enrollmentPage = courseRepository.findAllMyEnrollmentsByStatus(
                    studentEntity.getId(),
                    status,
                    pageable
            );
        }

        // Map enrollments to EnrollmentDTOs
        List<EnrollmentDTO> enrollmentDTOs = enrollmentPage.getContent().stream().map(enrollment -> {
            // Get course details from the relationship
            CourseEntity course = courseRepository.getReferenceById(enrollment.getCourseId());

            // Map to CourseDTO
            CourseDTO courseDTO = new CourseDTO(
                    course.getId(),
                    course.getInstructorId(),
                    course.getTitle(),
                    course.getShortDescription(),
                    course.getDescription(),
                    course.getImageUrl(),
                    course.getLanguage(),
                    course.getOriginalPrice(),
                    course.getOriginalPrice(), // not important
                    course.getAverageRating(),
                    course.getTotalStudents(),
                    course.getTotalViews(),
                    course.getIsPublish(),
                    course.getIsHot(),
                    course.getIsDiscount(),
                    course.getCreatedAt(),
                    course.getUpdatedAt()
            );

            // Map to EnrollmentDTO
            return new EnrollmentDTO(
                    enrollment.getId(),
                    courseDTO,
                    enrollment.getStatus(),
                    enrollment.getProgressPercent(),
                    enrollment.getEnrolledAt(),
                    enrollment.getCompletedAt()
            );
        }).collect(Collectors.toList());

        // Return paginated response
        return new PageImpl<>(enrollmentDTOs, pageable, enrollmentPage.getTotalElements());
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


        // save history view of course
        if(authentication != null)
        {
            // find student
            String username = authentication.getName();
            StudentEntity student = studentRepository.findByUsername(username);
            if(student != null)
            {
                // save history view
                StudentCourseViewEntity studentCourseViewEntity = new StudentCourseViewEntity();
                studentCourseViewEntity.setStudentId(student.getId());
                studentCourseViewEntity.setCourseId(id);
                studentCourseViewRepository.save(studentCourseViewEntity);
            }
        }

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
        List<ChapterEntity> chapterOfCourse = chapterRepository.findByCourseIdAndIsPublishTrueOrderByOrderIndexAsc(courseId);

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
            courseCurriculumDTO.setTotalLessons(lessonRepository.countByCourseIdAndIsPublishTrue(courseId));
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
            ChapterProgressEntity chapterProgress = chapterProgressRepository
                    .findByChapterIdAndStudentId(chapterDTO.getId() ,student.getId());
            if(chapterProgress == null) {
                chapterProgress = new ChapterProgressEntity();
            }
            ChapterProgressDTO chapterProgressDTO = new ChapterProgressDTO();
            chapterProgressDTO.setStatus(chapterProgress.getStatus());
            chapterProgressDTO.setCompletedAt(chapterProgress.getCompletedAt());
            chapterProgressDTO.setProgressPercent(chapterProgress.getProgressPercent());

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

        EnrollmentEntity enrollmentEntity = enrollmentRepository
                .findByStudentIdAndCourseIdAndIsActiveTrue(student.getId(), courseId);

        CourseCurriculumDTO courseCurriculumDTO = new CourseCurriculumDTO();
        courseCurriculumDTO.setChapters(chapters);
        courseCurriculumDTO.setTotalLessons(lessonRepository.countByCourseIdAndIsPublishTrue(courseId));
        if(enrollmentEntity == null) {
            return courseCurriculumDTO;
        }
        // find lesson progress of student
        courseCurriculumDTO.setCourseStatus(enrollmentEntity.getStatus());
        courseCurriculumDTO.setCourseProgress(enrollmentEntity.getProgressPercent());
        courseCurriculumDTO.setCompletedLessons(lessonProgressRepository.countCompletedByEnrollmentId(enrollmentEntity.getId()));
        courseCurriculumDTO.setRemainingLessons(courseCurriculumDTO.getTotalLessons() - courseCurriculumDTO.getCompletedLessons());
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
        return chapterRepository.findByCourseIdAndIsPublishTrueOrderByOrderIndexAsc(courseId);
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
    public LessonDraftEntity saveLessonDraft(LessonDraftEntity lessonEntity) {
        return lessonDraftRepository.save(lessonEntity);
    }

    @Override
    public VideoLessonEntity saveVideoLesson(VideoLessonEntity videoLessonEntity) {
        return videoLessonRepository.save(videoLessonEntity);
    }

    @Override
    public boolean isCompleteLesson(Long lessonId, Long studentId) {
        LessonProgressEntity lessonProgress = lessonProgressRepository.findByLessonIdAndStudentId(lessonId, studentId);
        if (lessonProgress != null) {
            return Objects.equals(lessonProgress.getStatus(), StatusType.COMPLETED);
        }
        return false;
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

    @Override
    public JSONArray getQuizContent(Long lessonId) {
        List<QuestionEntity> questions = questionRepository.findByLessonId(lessonId);
        JSONArray quizArray = new JSONArray();

        for (QuestionEntity question : questions) {
            JSONObject questionObj = new JSONObject();
            questionObj.put("id", question.getId());
            questionObj.put("text", question.getText());

            List<AnswerEntity> answers = answerRepository.findByQuestionId(question.getId());
            JSONArray answersArray = new JSONArray();
            for (AnswerEntity answer : answers) {
                JSONObject answerObj = new JSONObject();
                answerObj.put("id", answer.getId());
                answerObj.put("text", answer.getText());
                answerObj.put("isCorrect", answer.getIsCorrect());
                answersArray.add(answerObj);
            }
            questionObj.put("answers", answersArray);
            quizArray.add(questionObj);
        }

        return quizArray;
    }

    @Override
    @Transactional
    public void updateQuizLesson(Long lessonId, QuizRequest quizRequest) throws Exception {
        try {
            // Xóa các câu hỏi cũ không còn trong payload
            List<QuestionEntity> existingQuestions = questionRepository.findByLessonId(lessonId);
            List<Long> newQuestionIds = quizRequest.getQuestions().stream()
                    .map(QuestionRequest::getId)
                    .filter(Objects::nonNull)
                    .toList();

            for (QuestionEntity existingQuestion : existingQuestions) {
                if (!newQuestionIds.contains(existingQuestion.getId())) {
                    answerRepository.deleteByQuestionId(existingQuestion.getId());
                    questionRepository.delete(existingQuestion);
                }
            }

            // Thêm hoặc cập nhật các câu hỏi mới
            for (QuestionRequest questionRequest : quizRequest.getQuestions()) {
                QuestionEntity questionEntity = questionRequest.getId() != null
                        ? questionRepository.findById(questionRequest.getId()).orElse(new QuestionEntity())
                        : new QuestionEntity();

                questionEntity.setLessonId(lessonId);
                questionEntity.setText(questionRequest.getText());
                questionEntity = questionRepository.save(questionEntity);

                // Xóa các đáp án cũ không còn trong payload
                List<AnswerEntity> existingAnswers = answerRepository.findByQuestionId(questionEntity.getId());
                List<Long> newAnswerIds = questionRequest.getAnswers().stream()
                        .map(AnswerRequest::getId)
                        .filter(Objects::nonNull)
                        .toList();

                for (AnswerEntity existingAnswer : existingAnswers) {
                    if (!newAnswerIds.contains(existingAnswer.getId())) {
                        answerRepository.delete(existingAnswer);
                    }
                }

                // Thêm hoặc cập nhật các đáp án mới
                for (AnswerRequest answerRequest : questionRequest.getAnswers()) {
                    AnswerEntity answerEntity = answerRequest.getId() != null
                            ? answerRepository.findById(answerRequest.getId()).orElse(new AnswerEntity())
                            : new AnswerEntity();

                    answerEntity.setQuestionId(questionEntity.getId());
                    answerEntity.setText(answerRequest.getText());
                    answerEntity.setIsCorrect(answerRequest.getIsCorrect());
                    answerRepository.save(answerEntity);
                }
            }
        }catch (Exception e) {
            throw new Exception("Error while updating quiz lesson: " + e.getMessage(), e);
        }


    }

    @Override
    public boolean submitLesson(Long courseId, Long chapterId, long lessonId, StudentEntity student) {

        EnrollmentEntity enrollmentEntity = enrollmentRepository
                .findByStudentIdAndCourseIdAndIsActiveTrue(student.getId(), courseId);

        LessonProgressEntity lessonProgressEntity = lessonProgressRepository
                .findByLessonIdAndStudentId(lessonId, student.getId());

        if(lessonProgressEntity != null) {
            return true;
        }

        if(enrollmentEntity == null) {
            return false;
        }

        // find chapter first
        ChapterEntity chapterEntity = chapterRepository.findByIdAndCourseIdAndIsPublishTrue(chapterId, courseId).orElse(null);
        if(chapterEntity == null) {
            return false;
        }

        ChapterProgressEntity chapterProgressEntity = chapterProgressRepository.findByChapterIdAndStudentId(chapterId, student.getId());
        if(chapterProgressEntity == null) {
            chapterProgressEntity = new ChapterProgressEntity();
            chapterProgressEntity.setChapterId(chapterId);
            chapterProgressEntity.setStudentId(student.getId());
            chapterProgressEntity.setStatus(StatusType.NOT_STARTED);
            chapterProgressEntity.setProgressPercent(0.0);
            chapterProgressEntity.setEnrollmentId(enrollmentEntity.getId());
            try
            {
                chapterProgressEntity = chapterProgressRepository.save(chapterProgressEntity);
            }catch (Exception e)
            {
                LogService.getgI().error(e);
                return false;
            }
        }

        // lesson progress
        lessonProgressEntity = new LessonProgressEntity();
        lessonProgressEntity.setLessonId(lessonId);
        lessonProgressEntity.setStudentId(student.getId());
        lessonProgressEntity.setStatus(StatusType.COMPLETED); // not_started, completed
        lessonProgressEntity.setChapterProgressId(chapterProgressEntity.getId());
        lessonProgressRepository.save(lessonProgressEntity);

        // calculate progress percent

        // total lesson of chapter
        Long totalLesson = lessonRepository.countByChapterIdAndIsPublishTrue(chapterId);

        // total lesson completed in chapter
        Long totalLessonCompletedInChapter = lessonProgressRepository
                .countByChapterProgressIdAndStudentId(chapterProgressEntity.getId(), student.getId());

        double progressPercent = (double) totalLessonCompletedInChapter / totalLesson * 100;

        // update progress percent
        chapterProgressEntity.setProgressPercent(progressPercent);
        if(progressPercent == 100) {
            chapterProgressEntity.setStatus(StatusType.COMPLETED);
            chapterProgressEntity.setCompletedAt(LocalDateTime.now());
        } else {
            chapterProgressEntity.setStatus(StatusType.IN_PROGRESS);
        }


        chapterProgressEntity = chapterProgressRepository.save(chapterProgressEntity);

        // update enrollment status
        // total chapter of course
        Long totalLessonOfCourse = lessonRepository.countByCourseIdAndIsPublishTrue(courseId);

        // total chapter completed in course
        Long totalLessonCompletedInCourse = lessonProgressRepository
                .countCompletedByEnrollmentId(enrollmentEntity.getId());

        // calculate progress percent
        double courseProgressPercent = (double) totalLessonCompletedInCourse / totalLessonOfCourse * 100;
        enrollmentEntity.setProgressPercent(courseProgressPercent);
        if(courseProgressPercent == 100) {
            enrollmentEntity.setStatus(StatusType.COMPLETED);
        } else {
            enrollmentEntity.setStatus(StatusType.IN_PROGRESS);
        }

        enrollmentEntity.setCompletedAt(LocalDateTime.now());

        enrollmentRepository.save(enrollmentEntity);

        return true;
    }

    @Override
    public CourseFeedbackEntity getCourseFeedback(Long courseId, Long studentId) {
        return courseFeedbackRepository.findByStudentIdAndCourseIdAndIsHiddenFalse(studentId, courseId).orElse(null);
    }

    @Override
    public List<EnrolledCourseDTO> getEnrolledCoursesByStudentId(Long studentId) {
        // Join courses with enrollments to get enrollment details
        List<Object[]> results = courseRepository.findEnrolledCoursesByStudentId(studentId);

        List<EnrolledCourseDTO> enrolledCourses = new ArrayList<>();
        for (Object[] result : results) {
            CourseEntity course = (CourseEntity) result[0];
            EnrollmentEntity enrollment = (EnrollmentEntity) result[1];

            EnrolledCourseDTO dto = new EnrolledCourseDTO();
            dto.setCourseId(course.getId());
            dto.setTitle(course.getTitle());
            dto.setImageUrl(course.getImageUrl());
            dto.setEnrolledAt(enrollment.getEnrolledAt());
            dto.setProgressPercent(enrollment.getProgressPercent());
            dto.setIsActive(enrollment.getIsActive());
            dto.setStatus(enrollment.getStatus());
            dto.setRating(enrollment.getIsRating());
            dto.setRatingStart(enrollment.getRating());
            enrolledCourses.add(dto);
        }

        return enrolledCourses;
    }

    @Override
    public Page<CourseWithInstructorDTO> findCoursesByFilters(String keyword, Boolean isHot, Boolean isPublish, Boolean isDiscount, Pageable pageable) {
        return courseRepository.findCoursesByFilters(keyword, isHot, isPublish, isDiscount, pageable);
    }

    @Override
    public AdminCourseDetailDTO getCourseDetail(Long courseId) {
        return courseRepository.findCourseDetailById(courseId).orElse(null);
    }

    @Override
    public List<AdminChapterDTO> getCourseContent(Long courseId) {
        // Fetch chapters for the course
        List<ChapterEntity> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
        if (chapters.isEmpty()) {
            return new ArrayList<>();
        }

        // Map chapters to DTOs
        return chapters.stream().map(chapter -> {
            AdminChapterDTO chapterDTO = new AdminChapterDTO();
            chapterDTO.setId(chapter.getId());
            chapterDTO.setTitle(chapter.getTitle());
            chapterDTO.setOrderIndex(chapter.getOrderIndex());
            chapterDTO.setIsPublish(chapter.getIsPublish());

            // Fetch lessons for the chapter
            List<LessonEntity> lessons = lessonRepository.findByChapterIdOrderByOrderIndex(chapter.getId());
            List<AdminLessonDTO> lessonDTOs = lessons.stream().map(lesson -> {
                AdminLessonDTO lessonDTO = new AdminLessonDTO();
                lessonDTO.setId(lesson.getId());
                lessonDTO.setTitle(lesson.getTitle());
                lessonDTO.setType(lesson.getType());
                lessonDTO.setOrderIndex(lesson.getOrderIndex());
                lessonDTO.setIsPublish(lesson.getIsPublish());

                // Handle video lessons
                if ("video".equals(lesson.getType())) {
                    VideoLessonEntity videoLesson = videoLessonRepository.findByLessonId(lesson.getId());
                    if (videoLesson != null) {
                        AdminVideoLessonDTO videoLessonDTO = new AdminVideoLessonDTO();
                        videoLessonDTO.setVideoUrl(videoLesson.getVideoUrl());
                        videoLessonDTO.setDuration(videoLesson.getDuration());
                        lessonDTO.setVideoLesson(videoLessonDTO);
                    }
                }
                // Handle quiz lessons
                else if ("quiz".equals(lesson.getType())) {
                    List<QuestionEntity> questions = questionRepository.findByLessonId(lesson.getId());
                    List<AdminQuestionDTO> questionDTOs = questions.stream().map(question -> {
                        AdminQuestionDTO questionDTO = new AdminQuestionDTO();
                        questionDTO.setId(question.getId());
                        questionDTO.setText(question.getText());

                        // Fetch answers for the question
                        List<AnswerEntity> answers = answerRepository.findByQuestionId(question.getId());
                        List<AdminAnswerDTO> answerDTOs = answers.stream().map(answer -> {
                            AdminAnswerDTO answerDTO = new AdminAnswerDTO();
                            answerDTO.setId(answer.getId());
                            answerDTO.setText(answer.getText());
                            answerDTO.setIsCorrect(answer.getIsCorrect());
                            return answerDTO;
                        }).collect(Collectors.toList());
                        questionDTO.setAnswers(answerDTOs);
                        return questionDTO;
                    }).collect(Collectors.toList());
                    lessonDTO.setQuestions(questionDTOs);
                }

                return lessonDTO;
            }).collect(Collectors.toList());

            chapterDTO.setLessons(lessonDTOs);
            return chapterDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public LessonEntity getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElse(null);
    }

    @Override
    public List<ChapterEntity> updateChapterOrder(List<OrderRequest.Order> chapterOrders) {
        List<ChapterEntity> updatedChapters = new ArrayList<>();
        for (OrderRequest.Order chapterOrder : chapterOrders) {
            ChapterEntity chapter = chapterRepository.findById(chapterOrder.getId()).orElse(null);
            if (chapter != null) {
                chapter.setOrderIndex(chapterOrder.getOrderIndex());
                updatedChapters.add(chapter);
            }
        }
        return chapterRepository.saveAll(updatedChapters);
    }

    @Override
    public List<LessonEntity> updateLessonOrder(List<OrderRequest.Order> lessonOrders) {
        List<LessonEntity> updatedLessons = new ArrayList<>();
        for (OrderRequest.Order lessonOrder : lessonOrders) {
            LessonEntity lesson = lessonRepository.findById(lessonOrder.getId()).orElse(null);
            if (lesson != null) {
                lesson.setOrderIndex(lessonOrder.getOrderIndex());
                updatedLessons.add(lesson);
            }
        }
        return lessonRepository.saveAll(updatedLessons);
    }

    @Override
    public void deleteChapter(ChapterEntity chapter) {
        chapterRepository.delete(chapter);
    }

    @Override
    public List<CourseDTO> getRecommendationCourse(Authentication authentication) {
        if(authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            StudentEntity student = studentRepository.findByUsername(username);
            if(student != null) {
                try {
                    // Trích xuất danh sách courseIds
                    List<Long> viewedCourseIds = studentCourseViewRepository
                            .findTop10DistinctCourseIdsByStudentId(student.getId());

                    if (viewedCourseIds.isEmpty()) {
                        return List.of();
                    }

                    String responseStr = callRecommendationAPI(student.getId() ,viewedCourseIds);

                    // Parse response thành danh sách courseIds
                    List<Long> recommendedCourseIds = parseRecommendationResponse(responseStr);

                    // Nếu không có gợi ý từ API, trả về danh sách trống
                    if (recommendedCourseIds.isEmpty()) {
                        return List.of();
                    }

                    // Lấy thông tin chi tiết của các khóa học được gợi ý
                    List<CourseDTO> recommendedCourses = courseRepository.findCoursesByIdIn(recommendedCourseIds);

                    // Xử lý giá khuyến mãi cho các khóa học
                    return recommendedCourses.stream()
                            .map(this::processDiscountPrice)
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    LogService.getgI().info("[getRecommendationCourse] Error: " + e.getMessage());
                    return List.of(); // Trả về danh sách trống nếu có lỗi
                }
            }
        }
        return List.of();
    }

    @Override
    public LessonDraftEntity getLessonDraftById(Long lessonDraftId) {
        return lessonDraftRepository.findById(lessonDraftId).orElse(null);
    }

    @Override
    public void saveVideoLessonDraft(VideoLessonDraftEntity videoLessonDraft) {
        videoLessonDraftRepository.save(videoLessonDraft);
    }

    @Override
    public CodeLessonDraftEntity saveCodeLessonDraft(CodeLessonDraftEntity codeLessonDraft) {
        return codeLessonDraftRepository.save(codeLessonDraft);
    }

    @Override
    public void saveCodeLesson(CodeLessonEntity codeLesson) {
        codeLessonRepository.save(codeLesson);
    }

    @Override
    public CodeLessonEntity getCodeLesson(Long courseId, Long chapterId, Long lessonId) {
        return codeLessonRepository.findCodeLessonByCourseAndChapterAndLesson(
                courseId, chapterId, lessonId
        );
    }

    // Gọi API recommendation với danh sách courseIds
    private String callRecommendationAPI(Long studentId, List<Long> viewedCourseIds) {
        try {
            // Create a JSON object with user_id and course_ids
            JSONObject requestObj = new JSONObject();
            requestObj.put("user_id", studentId);
            requestObj.put("course_ids", viewedCourseIds);

            // Convert JSON object to string
            String requestJson = requestObj.toString();

            // Log the request
            LogService.getgI().info("[callRecommendationAPI] Request: " + requestJson);

            // Call API and wait for response
            String responseStr = webClient.post()
                    .uri(RECOMMENDATION_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Log the response
            LogService.getgI().info("[callRecommendationAPI] Response: " + responseStr);

            return responseStr;
        } catch (Exception e) {
            LogService.getgI().info("[callRecommendationAPI] Error: " + e.getMessage());
            return "[]";
        }
    }

    private List<Long> parseRecommendationResponse(String responseStr) {
        try {
            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            JSONObject jsonObject = (JSONObject) parser.parse(responseStr);

            JSONArray courseIdsArray = (JSONArray) jsonObject.get("course_ids");

            List<Long> courseIds = new ArrayList<>();
            if (courseIdsArray != null) {
                for (Object obj : courseIdsArray) {
                    if (obj instanceof Number) {
                        courseIds.add(((Number) obj).longValue());
                    } else if (obj instanceof String) {
                        try {
                            courseIds.add(Long.parseLong((String) obj));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            return courseIds;
        } catch (Exception e) {
            LogService.getgI().info("[parseRecommendationResponse] Error: " + e.getMessage());
            return List.of();
        }
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
