package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "instructor_payment_request_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorPaymentRequestLogEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long studentId;

        private String instructorName;

        private String instructorTitle;

        private String paymentMethodCode;

        private Integer price;

        private String transactionId;

        private Long orderCode;

        private String status;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        @PrePersist
        public void prePersist() {
            LocalDateTime now = LocalDateTime.now();
            this.createdAt = now;
        }

        @PreUpdate
        public void preUpdate() {
            LocalDateTime now = LocalDateTime.now();
            this.updatedAt = now;
        }
}
