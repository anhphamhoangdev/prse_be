package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.entity.QuizHistoryEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.QuizSubmitRequest;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.QuizService;
import com.hcmute.prse_be.service.StudentService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping(ApiPaths.QUIZ_API)
public class QuizAPI {

    private final QuizService quizService;

    private final CourseService courseService;

    private final StudentService studentService;

    @Autowired
    public QuizAPI(QuizService quizService, CourseService courseService, StudentService studentService) {
        this.quizService = quizService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @GetMapping("/content/{courseId}/{chapterId}/{lessonId}")
    public ResponseEntity<JSONObject> getQuizContent(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long lessonId,
            Authentication authentication
    ) {
        LogService.getgI().info("[QuizAPI] getQuizContent : courseId=" + courseId + ", chapterId=" + chapterId + ", lessonId=" + lessonId);

        try {
            // Kiểm tra quyền truy cập khóa học
            if (!courseService.checkCourseAccess(courseId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không có quyền truy cập khóa học"));
            }

            // Lấy nội dung quiz
            JSONArray quizContent = quizService.getQuizContent(lessonId);

            // Luôn trả về OK dù mảng rỗng
            JSONObject data = new JSONObject();
            data.put("quiz", quizContent); // quizContent có thể là []

            return ResponseEntity.ok(Response.success(data));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error("Lỗi khi lấy nội dung quiz"));
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(
            @RequestBody QuizSubmitRequest quizSubmitRequest,
            Authentication authentication
    ) {
        LogService.getgI().info("[QuizAPI] submitQuiz : requestBody=" + quizSubmitRequest.toString());

        try {
            // 1. Kiểm tra authentication
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Chưa đăng nhập"));
            }


            StudentEntity studentEntity = studentService.findByUsername(authentication.getName());
            if (studentEntity == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin người dùng"));
            }


            try {

                // save quiz
                quizService.saveQuizHistory(studentEntity, quizSubmitRequest);

                // update lesson progress if quiz score is equal or greater than 80%
                if(quizSubmitRequest.getScore() >= 80)
                {
                    courseService.submitLesson(
                                    quizSubmitRequest.getCourseId(),
                                    quizSubmitRequest.getChapterId(),
                                    quizSubmitRequest.getLessonId(),
                                    studentEntity
                            );
                }

                // then create progress
            } catch (Exception ignored) {

            }

            return ResponseEntity.ok(Response.success());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error("Lỗi khi nộp bài quiz: " + e.getMessage()));
        }
    }

    @GetMapping("/history/{lessonId}")
    public ResponseEntity<JSONObject> getQuizHistory(
            @PathVariable Long lessonId,
            Authentication authentication
    ) {
        LogService.getgI().info("[QuizAPI] getQuizHistory : lessonId=" + lessonId);

        try {
            // Kiểm tra quyền truy cập khóa học
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Chưa đăng nhập"));
            }

            StudentEntity studentEntity = studentService.findByUsername(authentication.getName());
            if (studentEntity == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Lấy lịch sử quiz
            List<QuizHistoryEntity> quizHistory = quizService.getQuizHistory(studentEntity.getId(), lessonId);


            JSONObject data = new JSONObject();
            data.put("quiz_history", quizHistory); // quizHistory có thể là []

            return ResponseEntity.ok(Response.success(data));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error("Lỗi khi lấy lịch sử quiz"));
        }
    }





}
