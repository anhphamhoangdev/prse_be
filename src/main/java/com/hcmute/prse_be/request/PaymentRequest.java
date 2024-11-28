package com.hcmute.prse_be.request;

import lombok.Data;

import java.util.List;

@Data
public class PaymentRequest {
    private Long checkoutDraftId;
    private Long paymentMethodId;
    private Long studentId;
    private Integer totalAmount;
    private List<PaymentItemRequest> items;
}
