package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.PaymentLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLogEntity, Long>{
}
