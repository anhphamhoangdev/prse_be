package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.PaymentRequestLogStatus;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.repository.*;
import com.hcmute.prse_be.request.PaymentItemRequest;
import com.hcmute.prse_be.request.PaymentRequest;
import com.hcmute.prse_be.request.PaymentUpdateStatusRequest;
import com.hcmute.prse_be.security.Endpoints;
import com.hcmute.prse_be.util.ConvertUtils;
import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.time.LocalDateTime;


@Service
public class PaymentServiceImpl implements PaymentService{

    private final PayOS payOS;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRequestLogRepository paymentRequestLogRepository;
    private final CheckoutDraftRepository checkoutDraftRepository;
    private final CartService cartService;


    // return url + cancelled url
    private final String returnUrl = Endpoints.FRONT_END_HOST +"/payment/success";
    private final String cancelUrl = Endpoints.FRONT_END_HOST +"/payment/cancel";
    private final PaymentLogRepository paymentLogRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final InstructorPlatformTransactionRepository instructorPlatformTransactionRepository;

    public PaymentServiceImpl(PayOS payOS, PaymentMethodRepository paymentMethodRepository, PaymentRequestLogRepository paymentRequestLogRepository, CheckoutDraftRepository checkoutDraftRepository, CartService cartService, PaymentLogRepository paymentLogRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, InstructorRepository instructorRepository, InstructorPlatformTransactionRepository instructorPlatformTransactionRepository) {
        this.payOS = payOS;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentRequestLogRepository = paymentRequestLogRepository;
        this.checkoutDraftRepository = checkoutDraftRepository;
        this.cartService = cartService;
        this.paymentLogRepository = paymentLogRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.instructorPlatformTransactionRepository = instructorPlatformTransactionRepository;
    }


    @Override
    public JSONObject createPayment(PaymentRequest paymentRequest, StudentEntity studentEntity) throws Exception {

        Long paymentMethodId = paymentRequest.getPaymentMethodId();
        // get payment method ra
        PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(paymentMethodId).orElse(null);


        if(paymentMethodEntity == null) {
            throw new Exception("Không tìm thấy phương thức thanh toán");
        }

        // tao request
        PaymentRequestLogEntity paymentRequestLogEntity = new PaymentRequestLogEntity();
        paymentRequestLogEntity.setCheckoutDraftId(paymentRequest.getCheckoutDraftId());
        paymentRequestLogEntity.setStudentId(studentEntity.getId());
        paymentRequestLogEntity.setPaymentMethodCode(paymentMethodEntity.getCode());
        paymentRequestLogEntity.setAmount(paymentRequest.getTotalAmount());
        paymentRequestLogEntity.setStatus(PaymentRequestLogStatus.NEW);
        paymentRequestLogEntity.setRequestData(JsonUtils.Serialize(paymentRequest));

        // save request
        paymentRequestLogEntity = paymentRequestLogRepository.save(paymentRequestLogEntity);

        Long orderCode = paymentRequestLogEntity.getId();

        String description = "STDID_" + studentEntity.getId() + "_ORDER_" + orderCode;

        String returnUrl = this.returnUrl; // gan tam
        String cancelUrl = this.cancelUrl; // gan tam

        final int price = paymentRequest.getTotalAmount();

        PaymentData paymentData = PaymentData
                .builder()
                .buyerName(studentEntity.getFullName())
                .buyerEmail(studentEntity.getEmail())
                .buyerPhone(studentEntity.getPhoneNumber())
                .orderCode(orderCode)
                .description(description)
                .amount(price)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();


        for (int i = 0; i < paymentRequest.getItems().size(); i++) {
            ItemData item = ItemData
                    .builder()
                    .name(paymentRequest.getItems().get(i).getTitle())
                    .price(ConvertUtils.toInt(paymentRequest.getItems().get(i).getPrice()))
                    .quantity(1)
                    .build();
            paymentData.addItem(item);
        }

        CheckoutResponseData data = payOS.createPaymentLink(paymentData);

        if (data != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("payment_info", data);
            paymentRequestLogEntity.setResponseData(JsonUtils.Serialize(jsonObject));
            paymentRequestLogEntity.setTransactionId(createTransactionId(orderCode, studentEntity.getId().toString()));
            CheckoutDraftEntity checkoutDraftEntity = checkoutDraftRepository.findById(paymentRequest.getCheckoutDraftId()).orElse(null);
            if (checkoutDraftEntity != null) {
                checkoutDraftEntity.setTransactionId(paymentRequestLogEntity.getTransactionId());
                checkoutDraftRepository.save(checkoutDraftEntity);
            }
            paymentRequestLogRepository.save(paymentRequestLogEntity);
            return jsonObject;
        }

        return null;
    }

    @Override
    public void updatePaymentStatus(PaymentUpdateStatusRequest data) {
        // orderCode = paymentRequestLogEntity.getId()
        Long orderCode = ConvertUtils.toLong(data.getOrderCode());
        PaymentRequestLogEntity paymentRequestLogEntity = paymentRequestLogRepository.findById(orderCode).orElse(null);
        if(paymentRequestLogEntity == null || !paymentRequestLogEntity.getStatus().equals(PaymentRequestLogStatus.NEW)) {
            return;
        }
        paymentRequestLogEntity.setStatus(data.getStatus());
        paymentRequestLogRepository.save(paymentRequestLogEntity);

        // create payment log if success
        if(data.getStatus().equals(PaymentRequestLogStatus.PAID)){
            PaymentLogEntity paymentLogEntity = new PaymentLogEntity();
            paymentLogEntity.setPaymentRequestLogId(orderCode);
            paymentLogEntity.setStudentId(paymentRequestLogEntity.getStudentId());
            paymentLogEntity.setPaymentMethodCode(paymentRequestLogEntity.getPaymentMethodCode());
            paymentLogEntity.setAmount(paymentRequestLogEntity.getAmount());
            paymentLogEntity.setRequestData(JsonUtils.Serialize(data));
            paymentLogEntity.setTransactionId(paymentRequestLogEntity.getTransactionId());

            PaymentRequest paymentRequest = JsonUtils.DeSerialize(paymentRequestLogEntity.getRequestData(), PaymentRequest.class);


            paymentLogEntity.setItems(JsonUtils.Serialize(paymentRequest.getItems()));
            paymentLogEntity = paymentLogRepository.save(paymentLogEntity);

            StudentEntity studentEntity = studentRepository.findById(paymentRequestLogEntity.getStudentId()).orElse(null);
            if (studentEntity != null) {
                for (PaymentItemRequest item : paymentRequest.getItems()) {
                    EnrollmentEntity enrollmentEntity = new EnrollmentEntity();
                    enrollmentEntity.setStudentId(studentEntity.getId());
                    enrollmentEntity.setCourseId(item.getCourseId());
                    enrollmentEntity.setEnrolledAt(LocalDateTime.now());
                    enrollmentEntity.setIsActive(true);
                    enrollmentEntity.setIsRating(false);
                    enrollmentEntity.setPaymentLogId(paymentLogEntity.getId());
                    enrollmentRepository.save(enrollmentEntity);

                    // kiem course de update total student va money cho instructor
                    CourseEntity courseEntity = courseRepository.findById(item.getCourseId()).orElse(null);
                    if(courseEntity != null) {
                        courseEntity.setTotalStudents(courseEntity.getTotalStudents() + 1);
                        courseEntity = courseRepository.save(courseEntity);
                        InstructorEntity instructorEntity = instructorRepository.findById(courseEntity.getInstructorId()).orElse(null);
                        if(instructorEntity != null) {

                            // update cho instructor
                            instructorEntity.setTotalStudent(instructorEntity.getTotalStudent() + 1);
                            instructorEntity.setMoney(instructorEntity.getMoney() + item.getPrice() * 0.7);
                            instructorEntity = instructorRepository.save(instructorEntity);


                            // update log cho instructor - platform
                            InstructorPlatformTransactionEntity instructorPlatformTransactionEntity = new InstructorPlatformTransactionEntity();
                            instructorPlatformTransactionEntity.setInstructorId(instructorEntity.getId());
                            instructorPlatformTransactionEntity.setInstructorMoney(item.getPrice() * 0.7);
                            instructorPlatformTransactionEntity.setPlatformMoney(item.getPrice() * 0.3);
                            instructorPlatformTransactionEntity.setCourseId(courseEntity.getId());
                            instructorPlatformTransactionEntity.setStudentId(studentEntity.getId());
                            instructorPlatformTransactionEntity.setSellPrice(item.getPrice());
                            instructorPlatformTransactionRepository.save(instructorPlatformTransactionEntity);
                        }
                    }
                }
                cartService.clearCart(studentEntity);
            }
        }
    }



    private String createTransactionId(Long orderCode, String studentId) {
        return "PAYOS" + "_STDID" + studentId + "_OID" + orderCode;
    }
}
