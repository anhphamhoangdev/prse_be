package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.constants.StatusType;
import com.hcmute.prse_be.dtos.CourseCurriculumDTO;
import com.hcmute.prse_be.dtos.CourseDTO;
import com.hcmute.prse_be.dtos.CourseFeedbackDTO;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.StudentService;
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


    @Autowired
    public CourseAPI(CourseService courseService, StudentService studentService) {
        this.courseService = courseService;
        this.studentService = studentService;
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

    @GetMapping(ApiPaths.COURSE_GET_LESSON)
    public ResponseEntity<JSONObject> getVideoLesson(
            @PathVariable("courseId") Long courseId,
            @PathVariable("chapterId") Long chapterId,
            @PathVariable("lessonId") Long lessonId,
            Authentication authentication
    ) {
        LogService.getgI().info("[CourseAPI] getVideoLesson : courseId=" + courseId + ", chapterId=" + chapterId + ", lessonId=" + lessonId);

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

            JSONObject data = new JSONObject();
            data.put("currentLesson", videoLesson);

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
            LessonProgressEntity lessonProgress = courseService.getLessonProgress(chapterId, lessonId);

            if(lessonProgress == null)
            {
                lessonProgress = new LessonProgressEntity();
                lessonProgress.setStudentId(student.getId());
                lessonProgress.setLessonId(lessonId);
                lessonProgress.setLastAccessedAt(LocalDateTime.now());
            }

            lessonProgress.setStatus(StatusType.COMPLETED);

            courseService.saveLessonProgress(lessonProgress);

            return ResponseEntity.ok(Response.success());

        } catch (Exception e) {
            return ResponseEntity.ok(Response.success(data));
        }
    }

    @GetMapping(ApiPaths.COURSE_GET_LIST_COURSE_STUDENT)
    public ResponseEntity<JSONObject> getMyCourses(Authentication authentication, @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "12") int size) {
        LogService.getgI().info("[CourseAPI] getMyCourses");

        try {
            // Lấy thông tin người dùng từ authentication
            StudentEntity studentEntity = studentService.findByUsername(authentication.getName());

            // Lấy danh sách khóa học của người dùng
            Page<CourseDTO> courses = courseService.getMyCourse(studentEntity, page, size);

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
        LogService.getgI().info("[CourseAPI] submitFeedback: " + data.toJSONString());
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
            CourseFeedbackEntity feedback = new CourseFeedbackEntity();
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
}
