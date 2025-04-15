package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.EnrollmentService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.StudentService;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentAPI {

    private final EnrollmentService enrollmentService;

    private final StudentService  studentService;

    public EnrollmentAPI(EnrollmentService enrollmentService, StudentService studentService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getEnrollmentStats(Authentication authentication) {
        LogService.getgI().info("[EnrollmentAPI] getEnrollmentStats" + authentication.getName());
        try {
            StudentEntity student = studentService.findByUsername(authentication.getName());
            if (student != null) {
                // Get enrollment stats for the student
                JSONObject enrollmentStats = new JSONObject();
                enrollmentStats.put("enrollment_stat", enrollmentService.getEnrollmentStats(student));
                return ResponseEntity.ok(Response.success(enrollmentStats));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }
        }catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }
    }
}
