package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.dtos.CodeExecutionRequestDto;
import com.hcmute.prse_be.dtos.CodeExecutionResponseDto;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CodeExecutionService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.StudentService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/code")
public class CodeExecutionAPI {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CodeExecutionService codeExecutionService;

//    @GetMapping("/languages")
//    public JSONObject getSupportedLanguages(Authentication authentication) {
//        LogService.getgI().info("[CodeExecutionAPI] getSupportedLanguages of: " + authentication.getName());
//        try {
//            String username = authentication.getName();
//            StudentEntity student = studentService.findByUsername(username);
//            if (student == null) {
//                return Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST);
//            }
//
//            JSONObject response = new JSONObject();
//            response.put("languages", codeExecutionService.getSupportedLanguages());
//
//            return Response.success(response);
//
//        } catch (Exception e) {
//            LogService.getgI().error("[CodeExecutionAPI] getSupportedLanguages error: " + e.getMessage());
//            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage());
//        }
//    }

    @PostMapping("/execute")
    public JSONObject executeCode(@RequestBody CodeExecutionRequestDto request, Authentication authentication) {
        LogService.getgI().info("[CodeExecutionAPI] executeCode of: " + authentication.getName() +
                " language: " + request.getLanguage() + " codeLength: " +
                (request.getCode() != null ? request.getCode().length() : 0));
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return Response.error(ErrorMsg.STUDENT_USERNAME_NOT_EXIST);
            }

            // Validate request
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                return Response.error("Code cannot be empty");
            }

            // Validate language
            List<String> supportedLanguages = Arrays.asList("python", "cpp", "java");
            if (!supportedLanguages.contains(request.getLanguage().toLowerCase())) {
                return Response.error("Unsupported language. Supported: " + String.join(", ", supportedLanguages));
            }

            // Execute code
            CodeExecutionResponseDto result = codeExecutionService.executeCode(request);

            LogService.getgI().info("[CodeExecutionAPI] executeCode completed. Success: " +
                    result.isSuccess() + " Time: " + result.getExecutionTime() + "ms");

            JSONObject response = new JSONObject();
            response.put("result", result);

            return Response.success(response);

        } catch (Exception e) {
            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG + e.getMessage());
        }
    }
}
