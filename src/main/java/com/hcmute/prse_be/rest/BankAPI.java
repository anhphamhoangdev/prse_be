package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.entity.BankEntity;
import com.hcmute.prse_be.service.BankService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/banks")
@RestController
public class BankAPI {

    private final BankService bankService;

    public BankAPI(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping()
    public List<BankEntity> getAllBanks() {
        return bankService.getAllBanks();
    }
}