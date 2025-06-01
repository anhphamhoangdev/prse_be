package com.hcmute.prse_be.rest;


import com.hcmute.prse_be.constants.*;
import com.hcmute.prse_be.dtos.*;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.request.CategoryOrderRequest;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final TicketService ticketService;

    private final PaymentService paymentService;
    private final WithdrawService withdrawService;
    private final CategoryService categoryService;

    private final LessonDraftService lessonDraftService;

    private final VideoLessonDraftService videoLessonDraftService;

    private final CodeLessonDraftService codeLessonDraftService;

    public AdminAPI(AdminService adminService, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtService jwtService, StudentService studentService, CourseService courseService, InstructorService instructorService, EnrollmentService enrollmentService, TicketService ticketService, PaymentService paymentService, WithdrawService withdrawService, CategoryService categoryService, LessonDraftService lessonDraftService, VideoLessonDraftService videoLessonDraftService, CodeLessonDraftService codeLessonDraftService) {
        this.adminService = adminService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.instructorService = instructorService;
        this.enrollmentService = enrollmentService;
        this.ticketService = ticketService;
        this.paymentService = paymentService;
        this.withdrawService = withdrawService;
        this.categoryService = categoryService;
        this.lessonDraftService = lessonDraftService;
        this.videoLessonDraftService = videoLessonDraftService;
        this.codeLessonDraftService = codeLessonDraftService;
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


    @GetMapping("/tickets")
    public ResponseEntity<JSONObject> getAllTickets(
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getAllTickets username: "+authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            List<TicketEntity> ticketPage = ticketService.getAllTickets();

            JSONObject response = new JSONObject();
            response.put("tickets", ticketPage);

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping("/tickets/{ticketId}")
    public ResponseEntity<JSONObject> updateStatusTicket(
            Authentication authentication,
            @PathVariable Long ticketId,
            @RequestBody TicketEntity ticketEntity
    ) {
        LogService.getgI().info("[AdminAPI] updateStatusTicket username: "+authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            TicketEntity ticket = ticketService.getTicketById(ticketId);

            if(ticket == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("KHONG TIM THAY TICKET"));
            }

            // Update ticket status
            ticket.setStatus(ticketEntity.getStatus());
            ticket.setResponse(ticketEntity.getResponse());

            if(ticketEntity.getStatus().equals(TicketStatusType.RESOLVED)) {
                ticket.setResolvedAt(LocalDateTime.now());
            }

            // Save the updated ticket
            ticketService.updateTicket(ticket);

            JSONObject response = new JSONObject();
            response.put("tickets", ticket);

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/invoices")
    public ResponseEntity<JSONObject> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String status,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getAllInvoices username: " + authentication.getName()
                + " page: " + page + " size: " + size + " sortBy: " + sortBy + " sortDir: " + sortDir
                + " transactionId: " + transactionId + " status: " + status);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Tạo đối tượng Sort
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            // Tạo đối tượng Pageable
            Pageable pageable = PageRequest.of(page, size, sort);
            // Gọi service để lấy danh sách hóa đơn phân trang với các điều kiện lọc
            Page<PaymentRequestLogEntity> paymentPage = paymentService.getFilteredPayments(transactionId, status, pageable);

            JSONObject response = new JSONObject();
            response.put("content", paymentPage.getContent());
            response.put("totalPages", paymentPage.getTotalPages());
            response.put("totalElements", paymentPage.getTotalElements());
            response.put("size", paymentPage.getSize());
            response.put("number", paymentPage.getNumber());

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<JSONObject> getAllCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isHot,
            @RequestParam(required = false) Boolean isPublish,
            @RequestParam(required = false) Boolean isDiscount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getAllCourses username: " + authentication.getName()
                + " page: " + page + " size: " + size + " sortBy: " + sortBy + " sortDir: " + sortDir
                + " keyword: " + keyword + " isHot: " + isHot + " isPublish: " + isPublish
                + " isDiscount: " + isDiscount);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Tạo đối tượng Sort
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            // Tạo đối tượng Pageable
            Pageable pageable = PageRequest.of(page, size, sort);

            // Gọi service để lấy danh sách khóa học theo filter
            Page<CourseWithInstructorDTO> coursesPage =
                    courseService.findCoursesByFilters(keyword, isHot, isPublish, isDiscount, pageable);

            JSONObject response = new JSONObject();
            response.put("content", coursesPage.getContent());
            response.put("totalPages", coursesPage.getTotalPages());
            response.put("totalElements", coursesPage.getTotalElements());
            response.put("size", coursesPage.getSize());
            response.put("number", coursesPage.getNumber());

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/courses/{id}/detail")
    public ResponseEntity<JSONObject> getCourseDetail(
            @PathVariable Long id,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getCourseDetail username: " + authentication.getName() + " id: " + id);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            AdminCourseDetailDTO courseDetail = courseService.getCourseDetail(id);
            if (courseDetail == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Course not found"));
            }
            JSONObject response = new JSONObject();
            response.put("courseDetail", courseDetail);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }

    }

    @PutMapping("/courses/{id}/updatePubish")
    public ResponseEntity<JSONObject> updatePubish(
            @PathVariable Long id,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updatePubish username: " + authentication.getName() + " id: " + id);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            CourseEntity course = courseService.getCourse(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Course not found"));
            }

            course.setIsPublish(!course.getIsPublish());

            CourseEntity updatedCourse = courseService.saveCourse(course);

            JSONObject response = new JSONObject();
            response.put("course", updatedCourse);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/courses/{id}/content")
    public ResponseEntity<JSONObject> getCourseContent(
            @PathVariable Long id,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getCourseContent username: " + authentication.getName() + " id: " + id);
        try {
            // Validate admin
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Fetch course content (chapters and lessons)
            List<AdminChapterDTO> chapters = courseService.getCourseContent(id);
            if (chapters == null || chapters.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Course content not found"));
            }

            // Build response
            JSONObject response = new JSONObject();
            response.put("chapters", chapters);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping("/courses/{courseId}/chapters/{chapterId}/updatePublish")
    public ResponseEntity<JSONObject> updateChapterPublish(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateChapterPublish username: " + authentication.getName() + " courseId: " + courseId + " chapterId: " + chapterId);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            ChapterEntity chapter = courseService.getChapterById(chapterId);
            if (chapter == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Chapter not found"));
            }

            chapter.setIsPublish(!chapter.getIsPublish());

            ChapterEntity updatedChapter = courseService.saveChapter(chapter);

            JSONObject response = new JSONObject();
            response.put("chapter", updatedChapter);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping("/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId}/updatePublish")
    public ResponseEntity<JSONObject> updateLessonPublish(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long lessonId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateLessonPublish username: " + authentication.getName() + " courseId: " + courseId + " chapterId: " + chapterId + " lessonId: " + lessonId);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            LessonEntity lesson = courseService.getLessonById(lessonId);
            if (lesson == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Lesson not found"));
            }

            lesson.setIsPublish(!lesson.getIsPublish());

            LessonEntity updatedLesson = courseService.saveLesson(lesson);

            JSONObject response = new JSONObject();
            response.put("lesson", updatedLesson);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<JSONObject> getCourseEnrollments(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getCourseEnrollments username: " + authentication.getName()
                + " courseId: " + courseId);
        try {
            // Kiểm tra quyền admin
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Lấy danh sách enrollment kèm thông tin student bằng một query duy nhất
            List<EnrollmentWithStudentDTO> enrollments = enrollmentService
                    .findAllEnrollmentsWithStudentByCourseId(courseId);

            JSONObject response = new JSONObject();
            response.put("enrollments", enrollments);
            response.put("totalElements", enrollments.size());

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping("/courses/enrollments/{enrollmentId}/toggle-active")
    public ResponseEntity<JSONObject> toggleEnrollmentActive(
            @PathVariable Long enrollmentId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] toggleEnrollmentActive username: " + authentication.getName()
                + " enrollmentId: " + enrollmentId);
        try {
            // Kiểm tra quyền admin
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Lấy thông tin enrollment
            EnrollmentEntity enrollment = enrollmentService.getEnrollmentById(enrollmentId);
            if (enrollment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Enrollment not found"));
            }

            enrollment.setIsActive(!enrollment.getIsActive());
            EnrollmentEntity updatedEnrollment = enrollmentService.saveEnrollment(enrollment);

            JSONObject response = new JSONObject();
            response.put("enrollment", updatedEnrollment);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/dashboard/process_statistics")
    public ResponseEntity<JSONObject> getProcessStatistics(
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getProcessStatistics username: " + authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            long totalNewWithdraws = withdrawService.countWithdrawsByStatus(WithDrawStatus.PENDING);
            long totalNewtickets = ticketService.countTicketsByStatus(TicketStatusType.NEW);
            JSONObject response = new JSONObject();
            response.put("totalNewWithdraws", totalNewWithdraws);
            response.put("totalNewTickets", totalNewtickets);
            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<JSONObject> getAllCategories(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "ALL") String status,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getAllCategories username: " + authentication.getName()
                + " search: " + search + " status: " + status);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            List<CategoryEntity> categories = categoryService.findAllWithFilters(
                    search, status
            );

            JSONObject response = new JSONObject();
            response.put("categories", categories);
            response.put("totalElements", categories.size());

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/categories/{categoryId}/sub-categories")
    public ResponseEntity<JSONObject> getSubCategoriesByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "ALL") String status,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getSubCategoriesByCategoryId categoryId: " + categoryId
                + " search: " + search + " status: " + status);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Find category by ID
            CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);
            if (categoryEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Category not found"));
            }

            List<SubCategoryEntity> subCategories = categoryService.getSubCategoriesByCategoryId(
                    categoryId, search, status
            );

            JSONObject response = new JSONObject();
            response.put("category", categoryEntity);
            response.put("subCategories", subCategories);
            response.put("totalElements", subCategories.size());

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<JSONObject> createCategory(
            @RequestBody CategoryEntity category,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] createCategory username: " + authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            CategoryEntity newCategory = categoryService.createCategory(category);

            JSONObject response = new JSONObject();
            response.put("category", newCategory);

            return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PostMapping("/categories/{categoryId}/sub-categories")
    public ResponseEntity<JSONObject> createSubCategory(
            @PathVariable Long categoryId,
            @RequestBody SubCategoryEntity subCategory,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] createSubCategory for categoryId: " + categoryId);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // Find category by ID
            CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);
            if (categoryEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Category not found"));
            }

            subCategory.setCategoryId(categoryId);
            SubCategoryEntity newSubCategory = categoryService.createSubCategory(subCategory);

            JSONObject response = new JSONObject();
            response.put("subCategory", newSubCategory);

            return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PostMapping("/categories/update-order")
    public ResponseEntity<JSONObject> updateCategoryOrder(
            @RequestBody CategoryOrderRequest request,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateCategoryOrder username: " + authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            List<CategoryEntity> updatedCategories = categoryService.updateCategoryOrder(request.getCategoryOrders());

            JSONObject response = new JSONObject();
            response.put("categories", updatedCategories);

            return ResponseEntity.ok(Response.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }


    @PostMapping("/categories/{categoryId}/sub-categories/update-order")
    public ResponseEntity<JSONObject> updateSubCategoryOrder(
            @RequestBody CategoryOrderRequest request,
            @PathVariable Long categoryId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateSubCategoryOrder username: " + authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            List<SubCategoryEntity> updatedCategories = categoryService.updateSubCategoryOrder(request.getSubCategoryOrders());

            JSONObject response = new JSONObject();
            response.put("subCategory", updatedCategories);

            return ResponseEntity.ok(Response.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping("/categories/{categoryId}/toggle-status")
    public ResponseEntity<JSONObject> updateCategoryToggleStatus(
            @PathVariable Long categoryId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateCategoryToggleStatus username: " + authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // find category by id
            CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);
            if (categoryEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Category not found"));
            }
            // toggle status
            categoryEntity.setIsActive(!categoryEntity.getIsActive());
            CategoryEntity updatedCategory = categoryService.save(categoryEntity);

            JSONObject response = new JSONObject();
            response.put("category", updatedCategory);

            return ResponseEntity.ok(Response.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping("/categories/{categoryId}/sub-categories/{subCategoriesId}/toggle-status")
    public ResponseEntity<JSONObject> updateSubCategoryToggleStatus(
            @PathVariable Long categoryId,
            @PathVariable Long subCategoriesId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateCategoryToggleStatus username: " + authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // find category by id
            SubCategoryEntity categoryEntity = categoryService.getSubCategoryById(subCategoriesId);
            if (categoryEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Category not found"));
            }
            // toggle status
            categoryEntity.setIsActive(!categoryEntity.getIsActive());
            SubCategoryEntity updatedCategory = categoryService.saveSubCategory(categoryEntity);

            JSONObject response = new JSONObject();
            response.put("category", updatedCategory);

            return ResponseEntity.ok(Response.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PutMapping("/categories/{categoryId}/sub-categories/{subCategoriesId}")
    public ResponseEntity<JSONObject> updateSubCategory(
            @RequestBody SubCategoryEntity updateSubCategory,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] updateCategoryToggleStatus username: " + authentication.getName());
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            // find category by id
            SubCategoryEntity subCategory = categoryService.getSubCategoryById(updateSubCategory.getId());
            if (subCategory == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Category not found"));
            }
            // toggle status
            subCategory.setName(updateSubCategory.getName());
            subCategory.setIsActive(updateSubCategory.getIsActive());
            subCategory.setCategoryId(updateSubCategory.getCategoryId());
            subCategory.setOrderIndex(updateSubCategory.getOrderIndex());
            SubCategoryEntity updatedSubCategory = categoryService.saveSubCategory(subCategory);

            JSONObject response = new JSONObject();
            response.put("subCategory", updatedSubCategory);

            return ResponseEntity.ok(Response.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }


    @GetMapping("/lessons-draft")
    public ResponseEntity<JSONObject> getAllLessonsDraft(
            @RequestParam(defaultValue = "ALL") String status,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getAllLessonsDraft username: " + authentication.getName()
                + " status: " + status);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            List<Map<String, Object>> lessonsDraft = lessonDraftService.findAllWithStatus(status);

            JSONObject response = new JSONObject();
            response.put("lessons", lessonsDraft);
            response.put("totalElements", lessonsDraft.size());

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/video-lessons-draft")
    public ResponseEntity<JSONObject> getVideoLessonDraft(
            @RequestParam Long lessonDraftId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getVideoLessonDraft username: " + authentication.getName()
                + " lessonDraftId: " + lessonDraftId);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            VideoLessonDraftEntity videoLesson = videoLessonDraftService.findByLessonDraftId(lessonDraftId);
            if (videoLesson == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Video lesson not found"));
            }

            JSONObject response = new JSONObject();
            response.put("videoLesson", videoLesson);

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @GetMapping("/code-lessons-draft")
    public ResponseEntity<JSONObject> getCodeLessonDraft(
            @RequestParam Long lessonDraftId,
            Authentication authentication
    ) {
        LogService.getgI().info("[AdminAPI] getCodeLessonDraft username: " + authentication.getName()
                + " lessonDraftId: " + lessonDraftId);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            CodeLessonDraftEntity codeLessonDraftEntity = codeLessonDraftService.findByLessonDraftId(lessonDraftId);
            if (codeLessonDraftEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Code lesson draft not found"));
            }

            JSONObject response = new JSONObject();
            response.put("codeLessonDraft", codeLessonDraftEntity);

            return ResponseEntity.ok(Response.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }


    @PatchMapping("/lessons-draft/{lessonDraftId}/approve")
    public ResponseEntity<JSONObject> approveLessonDraft(Authentication authentication,
                                                         @PathVariable Long lessonDraftId)
    {
        LogService.getgI().info("[AdminAPI] approveLessonDraft username: " + authentication.getName()
                + " lessonDraftId: " + lessonDraftId);
        try {
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            LessonDraftEntity lessonDraftEntity = courseService.getLessonDraftById(lessonDraftId);

            if (lessonDraftEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Lesson Draft not found"));
            }

            if(Objects.equals(lessonDraftEntity.getType(), LessonType.VIDEO)) {

                VideoLessonDraftEntity videoLessonDraft = videoLessonDraftService.findByLessonDraftId(lessonDraftId);
                if (videoLessonDraft == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Video lesson not found"));
                }

                // tao lesson moi
                List<LessonEntity> lessons = courseService.getLessonsByChapterId(lessonDraftEntity.getChapterId());
                LessonEntity lesson = new LessonEntity();
                lesson.setTitle(lessonDraftEntity.getTitle());
                lesson.setChapterId(lessonDraftEntity.getChapterId());
                lesson.setType(lessonDraftEntity.getType());
                lesson.setOrderIndex(lessons.size() + 1);
                lesson.setIsPublish(lessonDraftEntity.getIsPublish());
                lesson = courseService.saveLesson(lesson);


                // tao video lesson moi
                VideoLessonEntity videoLesson = new VideoLessonEntity();
                videoLesson.setLessonId(lesson.getId());
                videoLesson.setVideoUrl(videoLessonDraft.getVideoUrl());
                videoLesson.setDuration(videoLessonDraft.getDuration());
                courseService.saveVideoLesson(videoLesson);

                lessonDraftEntity.setStatus(LessonDraftStatus.APPROVED);
                lessonDraftService.save(lessonDraftEntity);
            }

            if(Objects.equals(lessonDraftEntity.getType(), LessonType.CODE)) {

                CodeLessonDraftEntity codeLessonDraftEntity = codeLessonDraftService.findByLessonDraftId(lessonDraftId);

                if(codeLessonDraftEntity == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Code lesson not found"));
                }

                // tao lesson moi
                List<LessonEntity> lessons = courseService.getLessonsByChapterId(lessonDraftEntity.getChapterId());
                LessonEntity lesson = new LessonEntity();
                lesson.setTitle(lessonDraftEntity.getTitle());
                lesson.setChapterId(lessonDraftEntity.getChapterId());
                lesson.setType(lessonDraftEntity.getType());
                lesson.setOrderIndex(lessons.size() + 1);
                lesson.setIsPublish(lessonDraftEntity.getIsPublish());
                lesson = courseService.saveLesson(lesson);


                CodeLessonEntity codeLesson = new CodeLessonEntity();
                codeLesson.setLessonId(lesson.getId());
                codeLesson.setLanguage(codeLessonDraftEntity.getLanguage());
                codeLesson.setContent(codeLessonDraftEntity.getContent());
                codeLesson.setInitialCode(codeLessonDraftEntity.getInitialCode());
                codeLesson.setSolutionCode(codeLessonDraftEntity.getSolutionCode());
                codeLesson.setExpectedOutput(codeLessonDraftEntity.getExpectedOutput());
                codeLesson.setHints(codeLessonDraftEntity.getHints());
                codeLesson.setDifficultyLevel(codeLessonDraftEntity.getDifficultyLevel().toLowerCase());
                codeLesson.setTestCaseInput(codeLessonDraftEntity.getTestCaseInput());
                codeLesson.setTestCaseOutput(codeLessonDraftEntity.getTestCaseOutput());
                codeLesson.setTestCaseDescription(codeLessonDraftEntity.getTestCaseDescription());

                courseService.saveCodeLesson(codeLesson);

                lessonDraftEntity.setStatus(LessonDraftStatus.APPROVED);
                lessonDraftService.save(lessonDraftEntity);
            }
            return ResponseEntity.ok(Response.success());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }

    @PatchMapping("/lessons-draft/{lessonDraftId}/reject")
    public ResponseEntity<JSONObject> rejectLessonDraft(Authentication authentication,
                                                         @PathVariable Long lessonDraftId,
                                                        @RequestBody  Map<String, String> request)
    {
        LogService.getgI().info("[AdminAPI] rejectLessonDraft username: " + authentication.getName()
                + " lessonDraftId: " + lessonDraftId);
        try {
            String rejectReason = request.get("rejectedReason");
            String email = authentication.getName();
            AdminEntity admin = adminService.findByEmail(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST));
            }

            LessonDraftEntity lessonDraftEntity = courseService.getLessonDraftById(lessonDraftId);

            if (lessonDraftEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Lesson Draft not found"));
            }

            lessonDraftEntity.setStatus(LessonDraftStatus.REJECTED);
            lessonDraftEntity.setRejectedReason(rejectReason);
            lessonDraftService.save(lessonDraftEntity);

            return ResponseEntity.ok(Response.success());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage()));
        }
    }




}
