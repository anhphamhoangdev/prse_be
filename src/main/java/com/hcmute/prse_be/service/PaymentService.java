package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.PaymentRequest;
import com.hcmute.prse_be.request.PaymentUpdateStatusRequest;
import net.minidev.json.JSONObject;

public interface PaymentService {
    JSONObject createPayment(PaymentRequest paymentRequest, StudentEntity studentEntity) throws Exception;

    void updatePaymentStatus(PaymentUpdateStatusRequest data);
}
