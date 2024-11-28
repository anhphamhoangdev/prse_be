package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {
    List<PaymentMethodEntity> findPaymentMethodEntitiesByIsActiveTrue();
}
