package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CheckoutDraftDTO {
    private Long id;
    private Long cartId;
    private Long studentId;
    private Double totalPrice;
    private Integer point;
    private Long discountCodeId;
    private Double totalDiscount;
    private Double totalPriceAfterDiscount;
    private String transactionId;
    private List<CartItemDTO> items;  // Thêm items
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
