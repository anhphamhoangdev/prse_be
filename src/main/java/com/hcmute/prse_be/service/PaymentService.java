package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.PaymentRequestLogEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.InstructorPaymentLogRequest;
import com.hcmute.prse_be.request.PaymentRequest;
import com.hcmute.prse_be.request.PaymentUpdateStatusRequest;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    JSONObject createPayment(PaymentRequest paymentRequest, StudentEntity studentEntity) throws Exception;
    JSONObject createInstructorPayment(InstructorPaymentLogRequest instructorPaymentLogRequest, StudentEntity studentEntity) throws Exception;

    void updatePaymentStatus(PaymentUpdateStatusRequest data);

    void updatePaymentStatusInstructor(PaymentUpdateStatusRequest data);

    List<PaymentRequestLogEntity> getAllPaymentRequestLogByStudentId(StudentEntity student);

    Double calculateTotalSpentByStudentId(Long studentId);

    Page<PaymentRequestLogEntity> getAllPayments(Pageable pageable) ;


    Page<PaymentRequestLogEntity> getFilteredPayments(String transactionId, String status, Pageable pageable);

}
