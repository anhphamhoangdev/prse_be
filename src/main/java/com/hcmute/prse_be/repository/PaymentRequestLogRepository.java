package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.PaymentRequestLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRequestLogRepository extends JpaRepository<PaymentRequestLogEntity, Long>{
    List<PaymentRequestLogEntity> findAllByStudentIdOrderByIdDesc(Long studentId);

}
