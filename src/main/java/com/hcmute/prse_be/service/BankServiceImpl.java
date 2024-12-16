package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.BankEntity;
import com.hcmute.prse_be.repository.BankRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankServiceImpl implements BankService{

    private final BankRepository bankRepository;

    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }


    @Override
    public List<BankEntity> getAllBanks() {
        return bankRepository.findAllByOrderByOrderIndexAsc();
    }
}
