package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.InstructorPaymentRequestLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorPaymentRequestLogRepository extends JpaRepository<InstructorPaymentRequestLogEntity, Long> {

    Optional<InstructorPaymentRequestLogEntity> findByOrderCode(Long orderCode);

}
