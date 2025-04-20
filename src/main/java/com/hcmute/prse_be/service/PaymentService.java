package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.PaymentRequestLogEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.InstructorPaymentLogRequest;
import com.hcmute.prse_be.request.PaymentRequest;
import com.hcmute.prse_be.request.PaymentUpdateStatusRequest;
import net.minidev.json.JSONObject;

import java.util.List;

public interface PaymentService {
    JSONObject createPayment(PaymentRequest paymentRequest, StudentEntity studentEntity) throws Exception;
    JSONObject createInstructorPayment(InstructorPaymentLogRequest instructorPaymentLogRequest, StudentEntity studentEntity) throws Exception;

    void updatePaymentStatus(PaymentUpdateStatusRequest data);

    void updatePaymentStatusInstructor(PaymentUpdateStatusRequest data);

    List<PaymentRequestLogEntity> getAllPaymentRequestLogByStudentId(StudentEntity student);
}
