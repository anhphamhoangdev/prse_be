package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.constants.StatusType;
import com.hcmute.prse_be.dtos.*;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.response.VideoLessonInfoResponse;
import com.hcmute.prse_be.service.*;
import com.hcmute.prse_be.util.ConvertUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(ApiPaths.COURSE_API)
public class CourseAPI {

    private final CourseService courseService;


    private final StudentService studentService;

    private final UserSubmissionService userSubmissionService;

    private final CodeExecutionService codeExecutionService;



    @Autowired
    public CourseAPI(CourseService courseService, StudentService studentService, UserSubmissionService userSubmissionService, CodeExecutionService codeExecutionService) {
        this.courseService = courseService;
        this.studentService = studentService;
        this.userSubmissionService = userSubmissionService;
        this.codeExecutionService = codeExecutionService;
    }

    @GetMapping(ApiPaths.COURSE_PATH_ID)
    public JSONObject getBasicDetailCourse(@PathVariable("id") Long id, Authentication authentication) {
        LogService.getgI().info("[CourseAPI] getBasicDetailCourse: " +id );
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("course", courseService.getDetailCourse(id, authentication));
        return Response.success(jsonObject);
    }

    @GetMapping(ApiPaths.COURSE_GET_FEEDBACK_ID)
    public JSONObject getCourseFeedback(
            @PathVariable("id") Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {

        LogService.getgI().info("[CourseAPI] getCourseFeedback : " + courseId);

        JSONObject response = new JSONObject();

        try {
            // Lấy feedback theo courseId và phân trang
            Page<CourseFeedbackDTO> feedbacks = courseService.getCourseFeedbacks(courseId, page, size);


            JSONObject data = new JSONObject();
            data.put("feedbacks", feedbacks.getContent());
            data.put("currentPage", feedbacks.getNumber());
            data.put("totalPages", feedbacks.getTotalPages());
            data.put("totalItems", feedbacks.getTotalElements());
            data.put("hasNext", feedbacks.hasNext());


            return Response.success(data);

        } catch (Exception e) {
            return Response.error("Không tìm thấy khóa học");
        }
    }


    @GetMapping(ApiPaths.COURSE_CURRICULUM_ID)
    public JSONObject getCourseCurriculum(
            @PathVariable("id") Long courseId,
            Authentication authentication
    )
    {
        LogService.getgI().info("[CourseAPI] getCourseCurriculum : " + courseId);

        try {

            CourseCurriculumDTO courseCurriculum  = courseService.getCourseCurriculum(courseId, authentication);

            JSONObject data = new JSONObject();

            data.put("chapters", courseCurriculum);

            return Response.success(data);

        } catch (Exception e) {
            return Response.error("Không tìm thấy khóa học");
        }
    }

    @GetMapping(ApiPaths.COURSE_GET_VIDEO_LESSON)
    public ResponseEntity<JSONObject> getVideoLesson(
            @PathVariable("courseId") Long courseId,
            @PathVariable("chapterId") Long chapterId,
            @PathVariable("lessonId") Long lessonId,
            Authentication authentication
    ) {
        LogService.getgI().info("[CourseAPI] getVideoLesson : courseId= " + courseId + ", chapterId= " + chapterId + ", lessonId= " + lessonId);

        try {

            // check coi no co vao khoa hoc chua neu chua thi tra ve forbidden
            if (!courseService.checkCourseAccess(courseId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không có quyền truy cập khóa học"));
            }

            // Lấy video lesson theo courseId, chapterId, lessonId
            VideoLessonEntity videoLesson = courseService.getVideoLesson(courseId, chapterId, lessonId);

            // Nếu không tìm thấy video lesson thì trả về not found
            if(videoLesson == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Không tìm thấy video lesson"));
            }

            // check xem complete chua thong qua progress cua lesson
            boolean isCompleted = courseService.isCompleteLesson(lessonId, studentService.findByUsername(authentication.getName()).getId());

            // tra ve VideoLessonInfoResponse
            VideoLessonInfoResponse videoLessonInfoResponse = new VideoLessonInfoResponse(videoLesson);
            videoLessonInfoResponse.setComplete(isCompleted);

            JSONObject data = new JSONObject();
            data.put("currentLesson", videoLessonInfoResponse);

            return ResponseEntity.ok(Response.success(data));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Không tìm thấy video lesson"));
        }
    }

    @GetMapping("/{courseId}/{chapterId}/{lessonId}/code")
    public ResponseEntity<JSONObject> getCodeLesson(
            @PathVariable("courseId") Long courseId,
            @PathVariable("chapterId") Long chapterId,
            @PathVariable("lessonId") Long lessonId,
            Authentication authentication
    ) {
        LogService.getgI().info("[CourseAPI] getCodeLesson : courseId= " + courseId + ", chapterId= " + chapterId + ", lessonId= " + lessonId);

        try {

            // check coi no co vao khoa hoc chua neu chua thi tra ve forbidden
            if (!courseService.checkCourseAccess(courseId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không có quyền truy cập khóa học"));
            }

            // Lấy video lesson theo courseId, chapterId, lessonId
            CodeLessonEntity codeLessonEntity = courseService.getCodeLesson(courseId, chapterId, lessonId);

            // Nếu không tìm thấy code lesson thì trả về not found
            if(codeLessonEntity == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Không tìm thấy video lesson"));
            }

            // check xem complete chua thong qua progress cua lesson
            boolean isCompleted = courseService.isCompleteLesson(lessonId, studentService.findByUsername(authentication.getName()).getId());

            // User last submission
            JSONObject data = new JSONObject();

            if(isCompleted)
            {
                StudentEntity student = studentService.findByUsername(authentication.getName());
                UserSubmissionEntity userSubmissionEntity = userSubmissionService
                        .getLatestSubmission(student.getId(), lessonId)
                        .orElse(null);
                data.put("lastSubmission", userSubmissionEntity);
            }

            data.put("currentLesson", codeLessonEntity);
            data.put("isCompleted", isCompleted);

            return ResponseEntity.ok(Response.success(data));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Không tìm thấy video lesson"));
        }
    }

    // submit lesson
    @PostMapping(ApiPaths.COURSE_SUBMIT_VIDEO)
    public ResponseEntity<JSONObject> submitLesson(@RequestBody JSONObject data, Authentication authentication) {
        LogService.getgI().info("[CourseAPI] submitLesson : " + data.toJSONString());

        try {
            // Lấy thông tin từ request
            Long courseId = Long.parseLong(data.getAsString("courseId"));
            Long chapterId = Long.parseLong(data.getAsString("chapterId"));
            Long lessonId = Long.parseLong(data.getAsString("lessonId"));

            // check coi no co vao khoa hoc chua neu chua thi tra ve forbidden
            if (!courseService.checkCourseAccess(courseId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không có quyền truy cập khóa học"));
            }

            // Lấy video lesson theo courseId, chapterId, lessonId
            VideoLessonEntity videoLesson = courseService.getVideoLesson(courseId, chapterId, lessonId);

            // Nếu không tìm thấy video lesson thì trả về not found
            if(videoLesson == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Không tìm thấy video lesson"));
            }

            // lay thong tin user
            StudentEntity student = studentService.findByUsername(authentication.getName());
            // Lấy thông tin lesson progress
            boolean submitLesson = courseService.submitLesson(courseId, chapterId, lessonId, student);

            if(submitLesson)
                return ResponseEntity.ok(Response.success());
            else
                return ResponseEntity.ok(Response.error("Đã có lỗi xảy ra khi submit lesson !"));
        } catch (Exception e) {
            return ResponseEntity.ok(Response.error("Đã có lỗi xảy ra khi submit lesson !"));
        }
    }

    @GetMapping(ApiPaths.COURSE_GET_LIST_COURSE_STUDENT)
    public ResponseEntity<JSONObject> getMyCourses(Authentication authentication,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "12") int size,
                                                   @RequestParam(defaultValue = "all") String status) {
        LogService.getgI().info("[CourseAPI] getMyCourses : " + authentication.getName() + " page: " + page + " size: " + size + " status: " + status);

        try {
            // Lấy thông tin người dùng từ authentication
            StudentEntity studentEntity = studentService.findByUsername(authentication.getName());

            // Lấy danh sách khóa học của người dùng
            Page<EnrollmentDTO> courses = courseService.getMyCourse(studentEntity, status, page, size);

            JSONObject data = new JSONObject();
            data.put("courses", courses);
            return ResponseEntity.ok(Response.success(data));

        } catch (Exception e) {
            return ResponseEntity.ok(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    // submit feedback
    @PostMapping(ApiPaths.COURSE_SUBMIT_FEEDBACK)
    public ResponseEntity<JSONObject> submitFeedback(@RequestBody JSONObject data, Authentication authentication) {
        LogService.getgI().info("[CourseAPI] submitFeedback: username: "+authentication.getName()+" feedback: " + data.toJSONString());
        try {
            // Lấy thông tin từ request
            Long courseId = Long.parseLong(data.getAsString("courseId"));
            int rating = Integer.parseInt(data.getAsString("rating"));
            String comment = data.getAsString("comment");

            // Validate rating
            if (rating < 1 || rating > 5) {
                return ResponseEntity.badRequest()
                        .body(Response.error("Đánh giá phải từ 1 đến 5 sao"));
            }

            // Check quyền truy cập khóa học
            if (!courseService.checkCourseAccess(courseId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Bạn cần phải tham gia khóa học để đánh giá"));
            }

            // Kiểm tra xem khóa học có tồn tại không
            CourseEntity course = courseService.getCourse(courseId);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy khóa học"));
            }

            // Lấy user hiện tại
            StudentEntity student = studentService.findByUsername(authentication.getName());

            // if student have feedback

            // Kiem tra enrollment da co rating chua, neu co roi thi update lai, neu chua thi save lai
            EnrollmentEntity enrollment = courseService.findEnrollmentByStudentAndCourse(student, course);
            if (enrollment != null) {
                if (enrollment.getRating() != null) {
                    enrollment.setRating(ConvertUtils.toDouble(rating));
                    enrollment.setIsRating(true);
                }
                else {
                    enrollment.setRating(ConvertUtils.toDouble(rating));
                }
                courseService.saveEnrollment(enrollment);
            }

            // Tạo feedback mới
            CourseFeedbackEntity feedback = courseService.getCourseFeedback(courseId, student.getId());

            if(feedback == null)
            {
                feedback = new CourseFeedbackEntity();
            }
            feedback.setCourseId(courseId);
            feedback.setStudentId(student.getId());
            feedback.setRating(ConvertUtils.toDouble(rating));
            feedback.setComment(comment);
            feedback.setStudentAvatarUrl(student.getAvatarUrl());
            feedback.setStudentName(student.getFullName());

            // Lưu feedback
            courseService.saveFeedback(feedback);

            // Cập nhật rating trung bình của khóa học
            courseService.updateCourseAverageRating(course);

            return ResponseEntity.ok(Response.success());

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Response.error("Dữ liệu không hợp lệ"));
        } catch (Exception e) {
            LogService.getgI().error(e);
            return ResponseEntity.internalServerError()
                    .body(Response.error("Có lỗi xảy ra khi gửi đánh giá"));
        }
    }

    @GetMapping(ApiPaths.COURSE_GET_ALL_FEEDBACK)
    public ResponseEntity<JSONObject> getAllCourseFeedbacks(@PathVariable Long courseId) {
        LogService.getgI().info("[CourseAPI] getAllCourseFeedbacks : " + courseId);
        try {
            // Lấy danh sách feedback của khóa học
            JSONObject data = new JSONObject();
            data.put("feedbacks", courseService.getAllCourseFeedbacks(courseId));
            return ResponseEntity.ok(Response.success(data));

        } catch (Exception e) {
            return ResponseEntity.ok(Response.error("Không tìm thấy khóa học"));
        }
    }

    @PostMapping("/submit-code")
    public ResponseEntity<JSONObject> submitCodeLesson(@RequestBody JSONObject data, Authentication authentication) {
        LogService.getgI().info("[CourseAPI] submitCodeLesson : " + data.toJSONString());

        try {
            // 1. Nhận request
            Long courseId = Long.parseLong(data.getAsString("courseId"));
            Long chapterId = Long.parseLong(data.getAsString("chapterId"));
            Long lessonId = Long.parseLong(data.getAsString("lessonId"));
            String code = data.getAsString("code");
            String language = data.getAsString("language");
            String input = data.getAsString("input");
            String expectedOutput = data.getAsString("expectedOutput");

            if (!courseService.checkCourseAccess(courseId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập khóa học"));
            }

            StudentEntity student = studentService.findByUsername(authentication.getName());
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // 4. Validate code không rỗng
            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.ok(Response.error("Code cannot be empty"));
            }

            // 5. Cho chạy code
            CodeExecutionRequestDto executionRequest = new CodeExecutionRequestDto();
            executionRequest.setCode(code);
            executionRequest.setLanguage(language);
            executionRequest.setInput(input);
            executionRequest.setExpectedOutput(expectedOutput);

            CodeExecutionResponseDto executionResult = codeExecutionService.executeCode(executionRequest);

            LogService.getgI().info("[CourseAPI] Code execution completed. Success: " +
                    executionResult.isSuccess() + " isCorrect: " + executionResult.getIsCorrect());

            // 6. Lưu submission
            UserSubmissionEntity submission = userSubmissionService.createSubmission(
                    student.getId(),
                    courseId,
                    chapterId,
                    lessonId,
                    code,
                    language,
                    executionResult.getIsCorrect(),
                    executionResult.getExecutionTime(),
                    executionResult.getMemoryUsed()
            );

            // 7. Nếu 2 cái bằng nhau => correct và lưu lesson progress
            if (executionResult.getIsCorrect() != null && executionResult.getIsCorrect()) {
                boolean lessonProgressUpdated = courseService.submitLesson(courseId, chapterId, lessonId, student);
                LogService.getgI().info("[CourseAPI] Lesson progress updated: " + lessonProgressUpdated);
            }
            // 8. Nếu sai thì trả về thôi không lưu progress (đã handle ở trên)

            // 9. Return execution result (giống executeCode response format)
            JSONObject response = new JSONObject();
            response.put("result", executionResult);

            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.ok(Response.error("Đã có lỗi xảy ra khi submit code lesson!"));
        }
    }
}
