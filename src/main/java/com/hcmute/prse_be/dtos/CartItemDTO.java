package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long courseId;
    private Double originalPrice;
    private String title;
    private String shortDescription;
    private String imageUrl;
    private Double discountPrice;
    private Double averageRating;
    private Integer totalStudents;
    private Boolean isDiscount;


}
