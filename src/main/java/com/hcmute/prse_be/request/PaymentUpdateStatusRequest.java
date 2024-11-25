package com.hcmute.prse_be.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PaymentUpdateStatusRequest {
    private String code;
    private String id;
    private String status;
    private String orderCode;
    private boolean cancel;
}