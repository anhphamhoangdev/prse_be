package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.WithDrawEntity;
import com.hcmute.prse_be.repository.WithdrawRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<WithDrawEntity> getAllWithdrawsByInstructor(Long instructorId) {
        return withdrawRepository.findAllByInstructorIdOrderByIdDesc(instructorId);
    }

    @Override
    public long countWithdrawsByStatus(String status) {
        return withdrawRepository.countByStatus(status);
    }


}
