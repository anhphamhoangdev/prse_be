package com.hcmute.prse_be.rest;


import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.PaymentRequest;
import com.hcmute.prse_be.request.PaymentUpdateStatusRequest;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.PaymentService;
import com.hcmute.prse_be.service.StudentService;
import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.payos.PayOS;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentAPI {

    private final StudentService studentService;

    private final PaymentService paymentService;
    private final PayOS payOS;

    public PaymentAPI(StudentService studentService, PaymentService paymentService, PayOS payOS) {
        this.studentService = studentService;
        this.paymentService = paymentService;
        this.payOS = payOS;
    }


    @PostMapping("/create")
    public ResponseEntity<JSONObject> createPayment(@RequestBody PaymentRequest data, Authentication authentication) throws Exception {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Chưa đăng nhập"));
        }

        Long studentId = data.getStudentId();

        StudentEntity studentEntity = studentService.findById(studentId);

        if (studentEntity == null || !studentEntity.getUsername().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin người dùng"));
        }

        // API call to payment gateway
        JSONObject paymentResponse = paymentService.createPayment(data, studentEntity);
        return ResponseEntity.ok(Response.success(paymentResponse));
    }

    @PostMapping("/update-status")
    public ResponseEntity<JSONObject> qq(@RequestBody PaymentUpdateStatusRequest data, Authentication authentication) {
        try {
            // Log để kiểm tra payload nhận được
            paymentService.updatePaymentStatus(data);
            // Trả về response success
            return ResponseEntity.ok(Response.success());

        } catch (Exception e) {
            // Log error nếu có
            LogService.getgI().error(e);
            return ResponseEntity.ok(Response.error("Error processing callback"));
        }
    }

//    @PostMapping("/callbackUrl")
//    public ResponseEntity<?> handleCallback(@RequestBody String payload) {
//        try {
//            // Log để kiểm tra payload nhận được
//            LogService.getgI().info("Received PayOS callback at: {}" + LocalDateTime.now());
//            LogService.getgI().info("Callback payload: {}" + payload);
//
//            // Trả về response success
//            return ResponseEntity.ok(Response.success());
//
//        } catch (Exception e) {
//            // Log error nếu có
//            LogService.getgI().error(e);
//            return ResponseEntity.ok(Response.error("Error processing callback"));
//        }
//    }

//    // chua dung duoc
//    @PostMapping(path = "/confirm-webhook")
//    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
//        LogService.getgI().info("Received confirm webhook request at: {}" + LocalDateTime.now());
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode response = objectMapper.createObjectNode();
//        try {
//            String str = payOS.confirmWebhook(requestBody.get("webhookUrl"));
//            response.set("data", objectMapper.valueToTree(str));
//            response.put("error", 0);
//            response.put("message", "ok");
//            return response;
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("error", -1);
//            response.put("message", e.getMessage());
//            response.set("data", null);
//            return response;
//        }
//    }


}


