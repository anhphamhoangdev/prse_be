package com.hcmute.prse_be.rest;


import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.dtos.CartDTO;
import com.hcmute.prse_be.entity.CartEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.CartService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.StudentService;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(ApiPaths.CART_API)
public class CartAPI {

    private final StudentService studentService;

    private final CartService cartService;

    public CartAPI(StudentService studentService, CartService cartService) {
        this.studentService = studentService;
        this.cartService = cartService;
    }


    // GET /api/v1/cart - Lấy thông tin giỏ hàng
    @GetMapping(ApiPaths.CART_GET_CART)
    public ResponseEntity<JSONObject> getCart(Authentication authentication) {
        LogService.getgI().info("[CartAPI] getCartInfor of user: username = " + authentication.getName());
        // neu dang nhap thanh cong thi se co trong authentication thong tin cua user

        if(authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Chưa đăng nhập"));
        }


        StudentEntity studentEntity = studentService.findByUsername(authentication.getName());
        if(studentEntity == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin người dùng"));
        }

        // neu tim thay nguoi dung thi
        // lay ra gio hang cua nguoi dung
        CartDTO cartDTO = cartService.getCartByUsername(studentEntity);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cart", cartDTO);


        return ResponseEntity.ok(Response.success(jsonObject));
    }

    @PostMapping(ApiPaths.CART_ADD_ITEM)
    public ResponseEntity<JSONObject> addToCart(
            Authentication authentication,
            @RequestBody Map<String, Long> data
    ) {

        LogService.getgI().info("[CartAPI] addToCart: item = " + data.toString());
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Chưa đăng nhập"));
        }

        StudentEntity studentEntity = studentService.findByUsername(authentication.getName());
        if(studentEntity == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin người dùng"));
        }

        return ResponseEntity.ok(cartService.addItemToCart(studentEntity, data.get("courseId")));




    }




    // DELETE /api/cart/items/{itemId} - Xóa item khỏi giỏ
    @DeleteMapping(ApiPaths.CART_REMOVE_ITEM_ID)
    public ResponseEntity<JSONObject> removeFromCart(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        LogService.getgI().info("[CartAPI] Delete ItemId " + itemId);

        if(authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Chưa đăng nhập"));
        }


        StudentEntity studentEntity = studentService.findByUsername(authentication.getName());
        if(studentEntity == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin người dùng"));
        }

        cartService.removeItemFromCart(studentEntity, itemId);

        return ResponseEntity.ok(Response.success());
    }


    @GetMapping(ApiPaths.CART_COUNT_ITEM)
    public ResponseEntity<JSONObject> getCartItemCount(
            Authentication authentication
    ) {
        LogService.getgI().info("[CartAPI] CountItem username: "+authentication.getName());

        if(authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Chưa đăng nhập"));
        }


        StudentEntity studentEntity = studentService.findByUsername(authentication.getName());
        if(studentEntity == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin người dùng"));
        }

        long count = cartService.getCartItemCount(studentEntity.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", count);
        return ResponseEntity.ok(Response.success(jsonObject));
    }
}
