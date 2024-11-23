package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {

    private Long id;
    private Long studentId;
    private Double totalPrice;
    private List<CartItemDTO> items;

}
