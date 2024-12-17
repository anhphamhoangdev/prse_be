package com.hcmute.prse_be.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminWithdrawDTO {
    private Long id;
    private InstructorWithdrawDTO instructor;
    private Double amount;
    private String type;
    private String status;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
