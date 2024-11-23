package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CartItemDTO;
import com.hcmute.prse_be.dtos.CheckoutDraftDTO;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final CourseRepository courseRepository;

    private final CourseDiscountRepository courseDiscountRepository;
    private final CheckoutDraftRepository checkoutDraftRepository;


    public CheckoutServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, CourseRepository courseRepository, CourseDiscountRepository courseDiscountRepository, CheckoutDraftRepository checkoutDraftRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.courseRepository = courseRepository;
        this.courseDiscountRepository = courseDiscountRepository;
        this.checkoutDraftRepository = checkoutDraftRepository;
    }

    // repository


    @Override
    public CheckoutDraftDTO createCheckoutDraft(StudentEntity studentEntity, Long cartId) {
        // 1. Lấy cart của user
        // Tìm cart của user
        CartEntity cartEntity = cartRepository.findByStudentId(studentEntity.getId()).orElse(null);
        if (cartEntity == null) {
            cartEntity = new CartEntity();
            cartEntity.setStudentId(studentEntity.getId());
            cartRepository.save(cartEntity);
            return null;
        }

        // 3. Lấy tất cả cart items
        List<CartItemEntity> cartItems = cartItemRepository.findByCartId(cartEntity.getId());

        if (cartItems.isEmpty()) {
            return null;
        }

        // muc dich la in ra cac course trong cart them 1 lan nua cho dung thoi diem
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        Double totalPrice = 0.0; // tong tien
        Double totalDiscount = 0.0; // tong giam gia
        Double totalPriceAfterDiscount = 0.0; // tong tien sau khi giam gia
        for (CartItemEntity cartItem : cartItems) {

            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setId(cartItem.getId());
            cartItemDTO.setCourseId(cartItem.getCourseId());

            CourseEntity courseEntity = courseRepository.findById(cartItem.getCourseId()).orElse(null);
            if(courseEntity != null && courseEntity.getIsPublish()){
                CourseDiscountEntity courseDiscountEntity = courseDiscountRepository
                        .findLatestValidDiscount(courseEntity.getId(), LocalDateTime.now()).orElse(null);
                if(courseDiscountEntity != null)
                {
                    totalPrice += courseEntity.getOriginalPrice();
                    totalDiscount += (courseEntity.getOriginalPrice() - courseDiscountEntity.getDiscountPrice());

                    cartItemDTO.setDiscountPrice(courseDiscountEntity.getDiscountPrice());
                    cartItemDTO.setIsDiscount(true);
                }
                else
                {
                    totalPrice += courseEntity.getOriginalPrice();

                    cartItemDTO.setDiscountPrice(courseEntity.getOriginalPrice());
                    cartItemDTO.setIsDiscount(false);
                }

                cartItemDTO.setOriginalPrice(courseEntity.getOriginalPrice());
                cartItemDTO.setAverageRating(courseEntity.getAverageRating());
                cartItemDTO.setTitle(courseEntity.getTitle());
                cartItemDTO.setImageUrl(courseEntity.getImageUrl());
                cartItemDTO.setShortDescription(courseEntity.getShortDescription());
                cartItemDTO.setTotalStudents(courseEntity.getTotalStudents());
                cartItemDTOS.add(cartItemDTO);
            }
        }

        totalPriceAfterDiscount = totalPrice - totalDiscount;

        CheckoutDraftEntity checkoutDraftEntity = new CheckoutDraftEntity();
        checkoutDraftEntity.setCartId(cartId);
        checkoutDraftEntity.setStudentId(studentEntity.getId());
        checkoutDraftEntity.setTotalPrice(totalPrice);
        checkoutDraftEntity.setPoint(0); // hien tai set mac dinh la 0
        checkoutDraftEntity.setDiscountCodeId(null); // chua co discount code
        checkoutDraftEntity.setTotalDiscount(totalDiscount);
        checkoutDraftEntity.setTotalPriceAfterDiscount(totalPriceAfterDiscount);
        checkoutDraftEntity.setTransactionId("TEMP_"+ UUID.randomUUID()); // tạm thời set là transaction_id
        checkoutDraftEntity = checkoutDraftRepository.save(checkoutDraftEntity);

        CheckoutDraftDTO checkoutDraftDTO = toDTO(checkoutDraftEntity);
        checkoutDraftDTO.setItems(cartItemDTOS);

        return checkoutDraftDTO;
    }

    private CheckoutDraftDTO toDTO(CheckoutDraftEntity entity) {
        if (entity == null) return null;

        CheckoutDraftDTO dto = new CheckoutDraftDTO();
        dto.setId(entity.getId());
        dto.setCartId(entity.getCartId());
        dto.setStudentId(entity.getStudentId());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setPoint(entity.getPoint());
        dto.setDiscountCodeId(entity.getDiscountCodeId());
        dto.setTotalDiscount(entity.getTotalDiscount());
        dto.setTotalPriceAfterDiscount(entity.getTotalPriceAfterDiscount());
        dto.setTransactionId(entity.getTransactionId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}
