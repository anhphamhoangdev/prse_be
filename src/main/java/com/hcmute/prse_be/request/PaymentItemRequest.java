package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class PaymentItemRequest {
    private Long id;
    private Long courseId;
    private String title;
    private Double price;
}
