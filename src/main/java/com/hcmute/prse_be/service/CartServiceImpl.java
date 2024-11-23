package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CartDTO;
import com.hcmute.prse_be.dtos.CartItemDTO;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.repository.CartItemRepository;
import com.hcmute.prse_be.repository.CartRepository;
import com.hcmute.prse_be.repository.CourseDiscountRepository;
import com.hcmute.prse_be.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService{

    private final CourseDiscountRepository courseDiscountRepository;
    private final CartRepository cartRepository;
    private final CourseRepository courseRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CourseDiscountRepository courseDiscountRepository, CartRepository cartRepository, CourseRepository courseRepository, CartItemRepository cartItemRepository) {
        this.courseDiscountRepository = courseDiscountRepository;
        this.cartRepository = cartRepository;
        this.courseRepository = courseRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public CartDTO getCartByUsername(StudentEntity studentEntity) {

        // Tìm cart của user
        CartEntity cartEntity = cartRepository.findByStudentId(studentEntity.getId()).orElse(null);
        // Nếu không tìm thấy thì tạo mới cart
        if (cartEntity == null) {
            cartEntity = new CartEntity();
            cartEntity.setStudentId(studentEntity.getId());
            cartRepository.save(cartEntity);
        }

        // 3. Lấy tất cả cart items
        List<CartItemEntity> cartItems = cartItemRepository.findByCartId(cartEntity.getId());
        // neu cart item rong thi return luon
        CartDTO cartDTO = new CartDTO();
        if (cartItems.isEmpty()) {
            cartDTO.setId(cartEntity.getId());
            cartDTO.setStudentId(cartEntity.getStudentId());
            cartDTO.setTotalPrice(0.0);
            cartDTO.setItems(new ArrayList<>());
            return cartDTO;
        }

        // neu co cart item => bat dau lay cac course trong cart item
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        Double totalPrice = 0.0;
        for (CartItemEntity cartItem : cartItems) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setId(cartItem.getId());
            cartItemDTO.setCourseId(cartItem.getCourseId());
            CourseEntity courseEntity = courseRepository.findById(cartItem.getCourseId()).orElse(null);
            if(courseEntity != null && courseEntity.getIsPublish())
            {
                // tim discount neu co
                CourseDiscountEntity courseDiscountEntity = courseDiscountRepository
                        .findLatestValidDiscount(courseEntity.getId(), LocalDateTime.now()).orElse(null);

                if(courseDiscountEntity != null)
                {
                    cartItemDTO.setDiscountPrice(courseDiscountEntity.getDiscountPrice());
                    cartItemDTO.setIsDiscount(true);
                }
                else
                {
                    cartItemDTO.setDiscountPrice(courseEntity.getOriginalPrice());
                    cartItemDTO.setIsDiscount(false);
                }

                totalPrice += cartItemDTO.getDiscountPrice();
                cartItemDTO.setOriginalPrice(courseEntity.getOriginalPrice());
                cartItemDTO.setAverageRating(courseEntity.getAverageRating());
                cartItemDTO.setTitle(courseEntity.getTitle());
                cartItemDTO.setImageUrl(courseEntity.getImageUrl());
                cartItemDTO.setShortDescription(courseEntity.getShortDescription());
                cartItemDTO.setTotalStudents(courseEntity.getTotalStudents());
                cartItemDTOS.add(cartItemDTO);
            }
        }

        cartDTO.setId(cartEntity.getId());
        cartDTO.setStudentId(cartEntity.getStudentId());
        cartDTO.setItems(cartItemDTOS);
        cartDTO.setTotalPrice(totalPrice);
        return cartDTO;
    }

    @Override
    public void addItemToCart(StudentEntity studentEntity, Long courseId) {
    }

    @Override
    public void removeItemFromCart(StudentEntity studentEntity, Long cartItemId) {
        CartEntity cartEntity = cartRepository.findByStudentId(studentEntity.getId()).orElse(null);
        if (cartEntity == null) {
            cartEntity = new CartEntity();
            cartEntity.setStudentId(studentEntity.getId());
            cartRepository.save(cartEntity);
        }

        cartItemRepository.findById(cartItemId).ifPresent(cartItemRepository::delete);

    }
}
