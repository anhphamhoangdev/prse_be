package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.PaymentMethodEntity;

import java.util.List;

public interface PaymentMethodService {
    List<PaymentMethodEntity> getPaymentMethodActive();
}
