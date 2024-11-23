package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CartDTO;
import com.hcmute.prse_be.entity.StudentEntity;

public interface CartService {
    CartDTO getCartByUsername(StudentEntity studentEntity);

    void addItemToCart(StudentEntity studentEntity, Long courseId);

    void removeItemFromCart(StudentEntity studentEntity, Long courseId);
}
