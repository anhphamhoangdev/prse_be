package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.BankEntity;

import java.util.List;

public interface BankService {
    List<BankEntity> getAllBanks();
//    BankEntity getBankById(Long id);
//    BankEntity getBankByCode(String code);
//    BankEntity createBank(BankEntity bankEntity);
//    BankEntity updateBank(BankEntity bankEntity);
//    void deleteBank(Long id);
}
