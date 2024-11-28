package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.constants.ImageFolderName;
import com.hcmute.prse_be.dtos.RecentEnrollmentDTO;
import com.hcmute.prse_be.dtos.RevenueStatisticsDTO;
import com.hcmute.prse_be.dtos.WebSocketMessage;
import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.CourseFormDataRequest;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.*;
import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/instructor")
public class InstructorAPI {

    private final StudentService studentService;

    private final InstructorService instructorService;

    private final CloudinaryService cloudinaryService;

    private final CourseService courseService;

    private final WebSocketService webSocketService;


    public InstructorAPI(StudentService studentService, InstructorService instructorService, CloudinaryService cloudinaryService, CourseService courseService, WebSocketService webSocketService) {
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.cloudinaryService = cloudinaryService;
        this.courseService = courseService;
        this.webSocketService = webSocketService;
    }


    @GetMapping(ApiPaths.GET_PROFILE)
    public ResponseEntity<JSONObject> getProfile(Authentication authentication) {
        try {

            String username = authentication.getName();

            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());

            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            JSONObject response = new JSONObject();
            response.put("instructor", instructor);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping("/revenue")
    public ResponseEntity<JSONObject> getRevenueStatistics(
            @RequestParam(defaultValue = "6") int monthsCount,
            Authentication authentication
    ) {
        try {
            // Verify student
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Verify instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Get revenue statistics
            List<RevenueStatisticsDTO> statistics = instructorService.getRevenueStatistics(
                    instructor.getId(),
                    monthsCount
            );

            // Build response
            JSONObject response = new JSONObject();
            response.put("revenue_statistics", statistics);

            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping("/recent-enrollments")
    public ResponseEntity<?> getRecentEnrollments(Authentication authentication) {
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            List<RecentEnrollmentDTO> enrollments = instructorService.getRecentEnrollments(instructor.getId());

            JSONObject response = new JSONObject();
            response.put("recent_enrollments", enrollments);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @PostMapping("/upload-course")
    public ResponseEntity<JSONObject> uploadCourse(
            @RequestParam("image") MultipartFile image,
            @RequestParam("data") String data,
            Authentication authentication) {
        try {

            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Xử lý dữ liệu form
            CourseFormDataRequest courseFormData = JsonUtils.DeSerialize(data, CourseFormDataRequest.class);

            // tao khoa hoc
            CourseEntity course = courseService.createCourse(courseFormData, instructor);

            // upload anh
            String imagePath =
                    cloudinaryService.uploadImage(image, ImageFolderName.COURSE+"/"+course.getId());


            // luu anh
            course.setImageUrl(imagePath);
            courseService.saveCourse(course);

            JSONObject response = new JSONObject();
            response.put("course", course);

            // ban thu socket
            WebSocketMessage message = WebSocketMessage.uploadComplete(
                    "Khoá học đã được upload lên thành công", course.getTitle()
            );
            webSocketService.sendToInstructor(instructor.getId(), "/uploads", message);


            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi tải lên khóa học"));
        }
    }
}
