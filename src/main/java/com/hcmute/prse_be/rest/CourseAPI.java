package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.dtos.CourseCurriculumDTO;
import com.hcmute.prse_be.dtos.CourseFeedbackDTO;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CourseService;
import com.hcmute.prse_be.service.LogService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}
