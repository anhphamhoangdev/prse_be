package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class WithdrawRequest {
    private Double amount;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
}
