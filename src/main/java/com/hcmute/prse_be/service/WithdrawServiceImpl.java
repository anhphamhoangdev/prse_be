package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.WithDrawEntity;
import com.hcmute.prse_be.repository.WithdrawRepository;
import org.springframework.stereotype.Service;

@Service
public class WithdrawServiceImpl implements WithdrawService{


    private final WithdrawRepository withdrawRepository;

    public WithdrawServiceImpl(WithdrawRepository withdrawRepository) {
        this.withdrawRepository = withdrawRepository;
    }

    @Override
    public void saveWithdraw(WithDrawEntity withDrawEntity) {
        withdrawRepository.save(withDrawEntity);
    }
}
