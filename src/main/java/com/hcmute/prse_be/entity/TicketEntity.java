package com.hcmute.prse_be.entity;

import com.hcmute.prse_be.constants.TicketStatusType;
import com.hcmute.prse_be.constants.TicketType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_type", nullable = false)
    private String userType; // 'student', 'instructor'

    // Loại ticket
    @Column(name = "ticket_type", nullable = false)
    private String ticketType;

    @Column(name = "course_id", nullable = true)
    private Long courseId;

    @Column(name = "payment_log_id")
    private Long paymentLogId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "attachment")
    private String attachmentUrl;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at", nullable = true)
    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = TicketStatusType.NEW; // Trạng thái mặc định
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}