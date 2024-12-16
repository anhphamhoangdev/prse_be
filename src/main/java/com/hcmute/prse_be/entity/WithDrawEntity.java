package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdraws")
@Data
public class WithDrawEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String instructorId;

    private Double amount;

    private String type; // BANK hoặc STUDENT

    private String bankCode;

    private String bankName;

    private String accountNumber;

    private String accountHolder;

    private String status;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
