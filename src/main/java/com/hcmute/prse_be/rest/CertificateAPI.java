package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.StatusType;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.request.CertificateRequest;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.*;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/certificate")
public class CertificateAPI {

    private final CertificateService certificateService;

    private final CourseService courseService;

    private final InstructorService instructorService;

    private final StudentService studentService;

    public CertificateAPI(CertificateService certificateService, CourseService courseService, InstructorService instructorService, StudentService studentService) {
        this.certificateService = certificateService;
        this.courseService = courseService;
        this.instructorService = instructorService;
        this.studentService = studentService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateCertificate(@RequestBody CertificateRequest certificateRequest, Authentication authentication) {

        LogService.getgI().info("[CertificateAPI] generateCertificate for courseId: " + certificateRequest.getCourseId() + " by user: " + authentication.getName());

        try {


            Long courseId = certificateRequest.getCourseId();
            StudentEntity student = studentService.findByUsername(authentication.getName());
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }

            CourseEntity course = courseService.getCourse(courseId);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
            }

            // check if the certificate already exists
            CertificateEntity existingCertificate = certificateService.getCertificateByStudentAndCourse(student.getId(), courseId);
            if (existingCertificate != null) {
                JSONObject response = new JSONObject();
                response.put("certificate", existingCertificate);
                return ResponseEntity.ok().body(Response.success(response));
            }

            // check if the student enrolled and completed the course
            EnrollmentEntity enrollment = courseService.findEnrollmentByStudentAndCourse(student, course);
            if (enrollment == null) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Student not enrolled in the course");
            }

            if (!Objects.equals(enrollment.getStatus(), StatusType.COMPLETED)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Student has not completed the course");
            }

            // Generate and upload the certificate
            InstructorEntity instructor = instructorService.getInstructorById(course.getInstructorId());
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instructor not found");
            }

            // Create and upload the certificate
            CertificateEntity certificate = certificateService.createCertificateAndUploadCloudinary(student, course, instructor);
            if (certificate == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create certificate");
            }
            JSONObject response = new JSONObject();
            response.put("certificate", certificate);
            return ResponseEntity.ok().body(Response.success(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
