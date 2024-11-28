package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_request_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long checkoutDraftId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "payment_method_code") // luu code de de xem hon
    private String paymentMethodCode;

    private Integer point;

    private Integer amount;

    private String status;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData; // -- request tu client

    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData; // -- response tu server

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