package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CartDTO;
import com.hcmute.prse_be.entity.StudentEntity;
import net.minidev.json.JSONObject;

public interface CartService {
    CartDTO getCartByUsername(StudentEntity studentEntity);

    JSONObject addItemToCart(StudentEntity studentEntity, Long courseId);

    void removeItemFromCart(StudentEntity studentEntity, Long courseId);

    long getCartItemCount(Long studentId);

    void clearCart(StudentEntity studentEntity);
}
