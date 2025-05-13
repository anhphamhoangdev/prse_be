package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.WithDrawEntity;

import java.util.List;

public interface WithdrawService {
    void saveWithdraw(WithDrawEntity withDrawEntity);

    List<WithDrawEntity> getAllWithdrawsByInstructor(Long instructorId);

    long countWithdrawsByStatus(String status);
}
