package com.hcmute.prse_be.rest;


import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.dtos.*;
import com.hcmute.prse_be.entity.AdminEntity;
import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.LoginRequest;
import com.hcmute.prse_be.response.JwtResponse;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.*;
import com.hcmute.prse_be.util.CalculateUtils;
import com.hcmute.prse_be.util.ConvertUtils;
import com.hcmute.prse_be.service.AdminService;
import com.hcmute.prse_be.service.CustomUserDetailsService;
import com.hcmute.prse_be.service.JwtService;
import com.hcmute.prse_be.service.LogService;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping(ApiPaths.ADMIN_API)
@RestController
public class AdminAPI {

    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    private final StudentService studentService;
    private final CourseService courseService;
    private final InstructorService instructorService;
    private final EnrollmentService enrollmentService;

    private final PaymentService paymentService;

    public AdminAPI(AdminService adminService, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtService jwtService, StudentService studentService, CourseService courseService, InstructorService instructorService, EnrollmentService enrollmentService, PaymentService paymentService) {
        this.adminService = adminService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.instructorService = instructorService;
        this.enrollmentService = enrollmentService;
        this.paymentService = paymentService;
    }


    @PostMapping(ApiPaths.LOGIN)
    public JSONObject login(@RequestBody LoginRequest loginRequest) {
        LogService.getgI().info("[AdminAPI] loginAdmin: username = " + loginRequest.getUsername() + ", password = "+ loginRequest.getPassword()  );
        try {
            AdminEntity admin = adminService.findByEmail(loginRequest.getUsername());
            if (admin == null) {
                return Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST);
            }

            if (!admin.isActive()) {
                return Response.error(ErrorMsg.ACCOUNT_NOT_ACTIVATED);
            }


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
                final String jwt = jwtService.generateToken(userDetails, false); // remember me : false
                JSONObject response = new JSONObject();
                response.put("jwt", new JwtResponse(jwt));
                return Response.success(response);
            }

        } catch (BadCredentialsException e) {
            return Response.error(ErrorMsg.WRONG_PASSWORD);
        } catch (Exception e) {
            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage());
        }

        return Response.error(ErrorMsg.AUTHENTICATION_FAILED);
    }

    @GetMapping(ApiPaths.GET_PROFILE)
    public JSONObject getProfile(Authentication authentication) {
        LogService.getgI().info("[AdminAPI] getAdminProfile username: " + authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST);
            }

            JSONObject response = new JSONObject();
            response.put("admin", admin);
            return Response.success(response);

        } catch (Exception e) {
            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage());
        }
    }

    @GetMapping(ApiPaths.ADMIN_OVERVIEW)
    public ResponseEntity<JSONObject> getOverview(Authentication authentication) {
        LogService.getgI().info("[AdminAPI]getOverview username: "+authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            LocalDateTime now = LocalDateTime.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();
            LocalDateTime previousMonth = now.minusMonths(1);
            int previousYear = previousMonth.getYear();
            int previousMonthValue = previousMonth.getMonthValue();

            long totalUsers = studentService.getCountStudent();
            long totalCourses = courseService.getCountCourse();
            long totalInstructors = instructorService.getCountInstructor();
            double totalRevenue = adminService.getTotalRevenue();

            long userCurrentMonthRecords = studentService.countByYearAndMonth(currentYear, currentMonth);
            long userPreviousMonthRecords = studentService.countByYearAndMonth(previousYear, previousMonthValue);
            double userGrowthRate = CalculateUtils.calculateGrowthRate(userCurrentMonthRecords, userPreviousMonthRecords);

            long courseCurrentMonthRecords = courseService.countByYearAndMonth(currentYear, currentMonth);
            long coursePreviousMonthRecords = courseService.countByYearAndMonth(previousYear, previousMonthValue);
            double courseGrowthRate = CalculateUtils.calculateGrowthRate(courseCurrentMonthRecords, coursePreviousMonthRecords);

            long instructorCurrentMonthRecords = instructorService.countByYearAndMonth(currentYear, currentMonth);
            long instructorPreviousMonthRecords = instructorService.countByYearAndMonth(previousYear, previousMonthValue);
            double instructorGrowthRate = CalculateUtils.calculateGrowthRate(instructorCurrentMonthRecords, instructorPreviousMonthRecords);

            double totalRevenueCurrentMonth = adminService.getTotalRevenueByMonth(currentMonth, currentYear);
            double totalRevenuePreviousMonth = adminService.getTotalRevenueByMonth(previousMonthValue, previousYear);
            double revenueGrowthRate = CalculateUtils.calculateGrowthRate(totalRevenueCurrentMonth, totalRevenuePreviousMonth);

            JSONObject response = new JSONObject();
            response.put("totalUsers", totalUsers);
            response.put("totalCourses", totalCourses);
            response.put("totalInstructors", totalInstructors);
            response.put("totalRevenue", totalRevenue);
            response.put("userGrowthRate", userGrowthRate);
            response.put("courseGrowthRate", courseGrowthRate);
            response.put("instructorGrowthRate", instructorGrowthRate);
            response.put("revenueGrowthRate", revenueGrowthRate);

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }
    @GetMapping(ApiPaths.REVENUE)
    public ResponseEntity<JSONObject> getRevenueStatistics(
            @RequestParam(defaultValue = "6") int monthsCount,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getRevenue username: "+authentication.getName());
        try {
            // Verify student
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Get revenue statistics
            List<RevenueStatisticsDTO> statistics = adminService.getRevenueStatistics(
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

    @GetMapping(ApiPaths.GET_CATEGORY_DISTRIBUTION)
    public ResponseEntity<JSONObject> getCategoryDistribution(
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getCategoryDistribution username: "+authentication.getName());
        try {
            // Verify student
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Get revenue statistics
            List<CategoryStatisticDTO> statistics = adminService.getCourseDistribution();

            // Build response
            JSONObject response = new JSONObject();
            response.put("category_distribution", statistics);

            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping(ApiPaths.GET_STUDENTS)
    public ResponseEntity<JSONObject> getAllStudents(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getAllStudents username: "+authentication.getName()
        + " search: "+ search+" status: "+status + " role: "+ role+" page: "+page +" size: "+size);
        try {

            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }


            Page<StudentEntity> studentPage = studentService.findAllWithFilters(
                    search, status, role, page, size
            );

            JSONObject response = new JSONObject();
            response.put("content", studentPage.getContent());
            response.put("totalPages", studentPage.getTotalPages());
            response.put("totalElements", studentPage.getTotalElements());
            response.put("size", studentPage.getSize());
            response.put("number", studentPage.getNumber());

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/instructors")
    public ResponseEntity<JSONObject> getAllStudents(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getAllStudents username: "+authentication.getName()
                + " search: "+ search+" status: "+status + "  page: "+page +" size: "+size);
        try {

            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }


            Page<InstructorEntity> instructorPage = instructorService.findAllWithFilters(
                    search, status, page, size
            );

            JSONObject response = new JSONObject();
            response.put("content", instructorPage.getContent());
            response.put("totalPages", instructorPage.getTotalPages());
            response.put("totalElements", instructorPage.getTotalElements());
            response.put("size", instructorPage.getSize());
            response.put("number", instructorPage.getNumber());

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping(ApiPaths.UPDATE_STUDENT_STATUS)
    public ResponseEntity<JSONObject> changeStudentStatus(
            @PathVariable Long studentId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateStudentStatus id: " + studentId);
        try {

            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }


            StudentEntity student = studentService.findById(studentId);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
            }

            student.setIsActive(!student.getIsActive());
            studentService.save(student);
            return ResponseEntity.ok(Response.success());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping(ApiPaths.UPDATE_INSTRUCTOR_STATUS)
    public ResponseEntity<JSONObject> changeInstructorStatus(
            @PathVariable Long instructorId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] changeInstructorStatus id: " + instructorId);
        try {

            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }


            InstructorEntity instructorEntity = instructorService.getInstructorById(instructorId);
            if (instructorEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
            }

            instructorEntity.setIsActive(!instructorEntity.getIsActive());
            instructorService.save(instructorEntity);
            return ResponseEntity.ok(Response.success());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping(ApiPaths.GET_WITHDRAWS)
    public ResponseEntity<JSONObject> getAllWithDraw(
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getAllWithdraws username: "+authentication.getName() );
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            List<AdminWithdrawDTO> withdraws = adminService.getAllPendinglWithdraws();

            JSONObject response = new JSONObject();
            response.put("withdraws", withdraws);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PatchMapping(ApiPaths.UPDATE_WITHDRAW_STATUS)
    public ResponseEntity<JSONObject> updateWithdrawStatus(
            @PathVariable Long withdrawId,
            @RequestBody AdminWithdrawDTO withdrawDTO,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateWithdrawStatus withdrawId: " + withdrawId + ", instructorId: " + (withdrawDTO.getInstructor() != null ? withdrawDTO.getInstructor().getId() : null) + ", status: " + withdrawDTO.getStatus() + ", rejectionReason: " + withdrawDTO.getRejectionReason());
        try {
            // Verify admin
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Validate status
            if (!"COMPLETED".equals(withdrawDTO.getStatus()) && !"REJECTED".equals(withdrawDTO.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Invalid status. Must be COMPLETED or REJECTED."));
            }

            // If status is REJECTED, ensure rejectionReason is provided
            if ("REJECTED".equals(withdrawDTO.getStatus()) && (withdrawDTO.getRejectionReason() == null || withdrawDTO.getRejectionReason().isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Rejection reason is required for REJECTED status."));
            }

            // Validate instructor
            if (withdrawDTO.getInstructor() == null || withdrawDTO.getInstructor().getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Instructor information with id is required."));
            }

            // Update withdraw status and instructor balance (if COMPLETED)
            AdminWithdrawDTO updatedWithdraw = adminService.updateWithdrawStatus(withdrawId, withdrawDTO);

            if (updatedWithdraw == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Withdraw request not found."));
            }

            JSONObject response = new JSONObject();
            response.put("withdraw", updatedWithdraw);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }


    // manage student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<JSONObject> getStudentById(
            @PathVariable Long studentId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getStudentById id: " + studentId);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Find student by ID
            StudentEntity student = studentService.findById(studentId);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Student not found"));
            }

            // Create DTO to hold student profile data
            AdminStudentProfileDTO profileDTO = new AdminStudentProfileDTO();
            student.setPasswordHash(null);
            profileDTO.setStudent(student);

            // Check if student is also an instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(studentId);
            profileDTO.setInstructor(instructor);

            // Get enrolled courses
            List<EnrolledCourseDTO> enrolledCourses = courseService.getEnrolledCoursesByStudentId(studentId);
            profileDTO.setEnrolledCourses(enrolledCourses);

            // Calculate total spent
            Double totalSpent = paymentService.calculateTotalSpentByStudentId(studentId);
            profileDTO.setTotalSpent(totalSpent);

            JSONObject response = new JSONObject();
            response.put("studentProfile", profileDTO);

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<JSONObject> getInstructorById(
            @PathVariable Long instructorId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getInstructorById id: " + instructorId);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Find student by ID
            InstructorEntity instructorEntity = instructorService.getInstructorById(instructorId);
            if (instructorEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Instructor not found"));
            }

            StudentEntity student = studentService.findById(instructorEntity.getStudentId());
            if(student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Student not found"));
            }

            // Create DTO to hold student profile data
            AdminInstructorProfileDTO profileDTO = new AdminInstructorProfileDTO();
            student.setPasswordHash(null);
            profileDTO.setStudentAccount(student);
            profileDTO.setInstructor(instructorEntity);


            // Get enrolled courses
            profileDTO.setStudents(instructorService.getStudentsByInstructorId(instructorId));

            // get courses
            profileDTO.setCourses(courseService.getCoursesByInstructorId(instructorId));

            // total revenue
            profileDTO.setTotalRevenue(instructorService.getTotalRevenueOfInstructor(instructorEntity.getId()));

            JSONObject response = new JSONObject();
            response.put("instructorProfile", profileDTO);

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }



}
