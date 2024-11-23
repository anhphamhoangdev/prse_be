package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.dtos.CartDTO;
import com.hcmute.prse_be.dtos.CheckoutDraftDTO;
import com.hcmute.prse_be.entity.CheckoutDraftEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.CheckoutDraftRequest;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CheckoutService;
import com.hcmute.prse_be.service.StudentService;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutAPI {

    private final StudentService studentService;

    private final CheckoutService checkoutService;

    public CheckoutAPI(StudentService studentService, CheckoutService checkoutService) {
        this.studentService = studentService;
        this.checkoutService = checkoutService;
    }


    // create checkout_draft
    // POST /api/checkout/create
    // Request: {cartId: Long}
    @PostMapping("/create")
    public ResponseEntity<JSONObject> createCheckoutDraft(Authentication authentication, @RequestBody CheckoutDraftRequest checkoutDraftRequest) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Chưa đăng nhập"));
            }

            StudentEntity studentEntity = studentService.findByUsername(authentication.getName());
            if (studentEntity == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Nếu tìm thấy người dùng thì lấy ra giỏ hàng của người dùng
            CheckoutDraftDTO checkoutDraftDTO = checkoutService.createCheckoutDraft(studentEntity, checkoutDraftRequest.getCartId());

            if (checkoutDraftDTO == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Không tìm thấy thông tin giỏ hàng"));
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("checkout_draft", checkoutDraftDTO);

            return ResponseEntity.ok(Response.success(jsonObject));
        } catch (Exception e) {
            // In ra thông tin chi tiết của ngoại lệ
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error("Đã xảy ra lỗi: " + e.getMessage()));
        }
    }
}
