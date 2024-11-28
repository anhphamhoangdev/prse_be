package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.PaymentMethodEntity;
import com.hcmute.prse_be.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService{
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<PaymentMethodEntity> getPaymentMethodActive() {
        return  paymentMethodRepository.findPaymentMethodEntitiesByIsActiveTrue();
    }
}
