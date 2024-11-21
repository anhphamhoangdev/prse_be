package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.constants.ImageFolderName;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.LoginRequest;
import com.hcmute.prse_be.response.JwtResponse;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CloudinaryService;
import com.hcmute.prse_be.service.CustomUserDetailsService;
import com.hcmute.prse_be.service.JwtService;
import com.hcmute.prse_be.service.StudentService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequestMapping(ApiPaths.STUDENT_API)
@RestController
public class StudentAPI {

    private final StudentService studentService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public StudentAPI(StudentService studentService, AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetailsService customUserDetailsService, CloudinaryService cloudinaryService) {
        this.studentService = studentService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(ApiPaths.CHECK_EXIST_USERNAME)
    public boolean existsByUsername(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        return studentService.existsByUsername(username);
    }

    @PostMapping(ApiPaths.CHECK_EXIST_EMAIL)
    public boolean existsByEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        return studentService.existsByEmail(email);
    }

    @PostMapping(ApiPaths.CHECK_EXIST_PHONE_NUMBER)
    public boolean existsByPhoneNumber(@RequestBody Map<String, String> requestBody) {
        String phoneNumber = requestBody.get("phoneNumber");
        return studentService.existsByPhoneNumber(phoneNumber);
    }

    @GetMapping(ApiPaths.ACTIVATE_ACCOUNT)
    public JSONObject activateAccount(@RequestParam String email, @RequestParam String activateCode) {
        return studentService.activeAccount(email, activateCode);
    }

    @PostMapping(ApiPaths.REGISTER_ACCOUNT)
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

    @PostMapping(ApiPaths.LOGIN)
    public JSONObject login(@RequestBody LoginRequest loginRequest) {
        try {

            StudentEntity student = customUserDetailsService.findByUsername(loginRequest.getUsername());
            if (student == null) {
                return Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST);
            }

            if (student.getIsBlocked()) {
                return Response.error(ErrorMsg.ACCOUNT_BLOCKED);
            }

            if (!student.getIsActive()) {
                studentService.sendActiveEmail(student.getEmail(), student.getActiveCode());
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
                final String jwt = jwtService.generateToken(userDetails, loginRequest.isRememberMe());
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
        try {
            String username = authentication.getName();
            StudentEntity student = customUserDetailsService.findByUsername(username);
            if (student == null) {
                return Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST);
            }

            JSONObject response = new JSONObject();
            response.put("student", student);
            return Response.success(response);

        } catch (Exception e) {
            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage());
        }
    }

    @PostMapping(ApiPaths.UPDATE_AVATAR)
    public JSONObject updateAvatar(@RequestParam MultipartFile file)
    {
        try{
            Map uploadAvatar  = cloudinaryService.uploadFile(file, ImageFolderName.STUDENT_AVATAR_FOLDER);
            String imageUrl = (String) uploadAvatar.get("url");
            JSONObject response = new JSONObject();
            response.put("avatarUrl",imageUrl);
            return response;
        } catch (Exception e) {
            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage());
        }
    }

}
