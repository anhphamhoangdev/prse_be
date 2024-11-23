package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CheckoutDraftDTO;
import com.hcmute.prse_be.entity.CheckoutDraftEntity;
import com.hcmute.prse_be.entity.StudentEntity;

public interface CheckoutService {
    CheckoutDraftDTO createCheckoutDraft(StudentEntity studentEntity, Long cartId);
}
