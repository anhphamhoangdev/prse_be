package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.constants.StatusType;
import com.hcmute.prse_be.dtos.CourseCurriculumDTO;
import com.hcmute.prse_be.dtos.CourseFeedbackDTO;
import com.hcmute.prse_be.entity.LessonProgressEntity;
import com.hcmute.prse_be.entity.VideoLessonEntity;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/course")
public class CourseAPI {

    private final CourseService courseService;


    @Autowired
    public CourseAPI(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("{id}")
    public JSONObject getBasicDetailCourse(@PathVariable("id") Long id, Authentication authentication) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("course", courseService.getDetailCourse(id, authentication));
        return Response.success(jsonObject);
    }

    @GetMapping("/{id}/feedbacks")
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


    @GetMapping("{id}/curriculum")
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

    @GetMapping("/{courseId}/{chapterId}/{lessonId}/video")
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
    @PostMapping("/video/submit")
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

            // Lấy thông tin lesson progress
            LessonProgressEntity lessonProgress = courseService.getLessonProgress(chapterId, lessonId);

            if(lessonProgress == null)
            {
                lessonProgress = new LessonProgressEntity();
                lessonProgress.setChapterProgressId(chapterId);
                lessonProgress.setLessonId(lessonId);
                lessonProgress.setLastAccessedAt(LocalDateTime.now());
            }

            lessonProgress.setStatus(StatusType.COMPLEDTED);

            courseService.saveLessonProgress(lessonProgress);

            return ResponseEntity.ok(Response.success());

        } catch (Exception e) {
            return ResponseEntity.ok(Response.success(data));
        }
    }
}
