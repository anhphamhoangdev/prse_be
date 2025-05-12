package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.PaymentRequestLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRequestLogRepository extends JpaRepository<PaymentRequestLogEntity, Long>{
    List<PaymentRequestLogEntity> findAllByStudentIdOrderByIdDesc(Long studentId);

    Page<PaymentRequestLogEntity> findByTransactionIdContaining(String transactionId, Pageable pageable);
    Page<PaymentRequestLogEntity> findByStatus(String status, Pageable pageable);
    Page<PaymentRequestLogEntity> findByTransactionIdContainingAndStatus(String transactionId, String status, Pageable pageable);

}
