package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.LoginRequest;
import com.hcmute.prse_be.response.JwtResponse;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CustomUserDetailsService;
import com.hcmute.prse_be.service.JwtService;
import com.hcmute.prse_be.service.StudentService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/student")
@RestController
public class StudentAPI {

    private final StudentService studentService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public StudentAPI(StudentService studentService, AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.studentService = studentService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/existsByUsername")
    public boolean existsByUsername(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        return studentService.existsByUsername(username);
    }

    @PostMapping("/existsByEmail")
    public boolean existsByEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        return studentService.existsByEmail(email);
    }

    @PostMapping("/existsByPhoneNumber")
    public boolean existsByPhoneNumber(@RequestBody Map<String, String> requestBody) {
        String phoneNumber = requestBody.get("phoneNumber");
        return studentService.existsByPhoneNumber(phoneNumber);
    }

    @GetMapping("/activate")
    public JSONObject activateAccount(@RequestParam String email, @RequestParam String activateCode) {
        return studentService.activeAccount(email, activateCode);
    }

    @PostMapping("/register")
    public JSONObject registerUser(@RequestBody StudentEntity user) {
        JSONObject response = new JSONObject();

        try {
            StudentEntity newStudent = studentService.register(user);
            if(newStudent == null)
            {
                return Response.error(ErrorMsg.FAILED_REGISTER);
            }
            response.put("student", newStudent);
            return Response.success(response);
        }catch(Exception e)
        {
            return Response.error(ErrorMsg.FAILED_REGISTER);
        }
    }

    @PostMapping("/login")
    public JSONObject login(@RequestBody LoginRequest loginRequest) {
        try {

            StudentEntity student = customUserDetailsService.findByUsername(loginRequest.getUsername());
            if (student == null) {
                return Response.error("Tài khoản không tồn tại");
            }

            if (student.getIsBlocked()) {
                return Response.error(ErrorMsg.ACCOUNT_BLOCKED);
            }

            if (!student.getIsActive()) {
                studentService.sendActiveEmail(student.getEmail(), student.getActiveCode());
                return Response.error("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email!");
            }


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
                final String jwt = jwtService.generateToken(userDetails, loginRequest.isRememberMe());
                JSONObject response = new JSONObject();
                response.put("jwt", new JwtResponse(jwt));
                return Response.success(response);
            }

        } catch (BadCredentialsException e) {
            return Response.error("Sai mật khẩu");
        } catch (Exception e) {
            return Response.error("Lỗi hệ thống: " + e.getMessage());
        }

        return Response.error("Xác thực thất bại");
    }

    @GetMapping("/profile")
    public JSONObject getProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            StudentEntity student = customUserDetailsService.findByUsername(username);
            if (student == null) {
                return Response.error("Không tìm thấy thông tin user");
            }

            JSONObject response = new JSONObject();
            response.put("student", student);
            return Response.success(response);

        } catch (Exception e) {
            return Response.error("Lỗi khi lấy thông tin: " + e.getMessage());
        }
    }


}
