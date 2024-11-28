package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_request_log_id")
    private Long paymentRequestLogId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "payment_method_code")
    private String paymentMethodCode;

    private Integer point;

    private Integer amount;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData; // tam thoi chua dung

    @Column(name = "request_url", columnDefinition = "TEXT")
    private String requestUrl; // tam thoi chua dung

    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData; // tam thoi chua dung

    @Column(name = "response_url", columnDefinition = "TEXT")
    private String responseUrl; // tam thoi chua dung

    private String items;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}