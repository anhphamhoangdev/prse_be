package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class InstructorPaymentLogRequest {
    private String instructorName;
    private String instructorTitle;
    private Integer price;
    private Long paymentMethodId;

}
