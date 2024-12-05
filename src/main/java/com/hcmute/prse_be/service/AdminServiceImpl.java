package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.AdminEntity;
import com.hcmute.prse_be.repository.AdminRepository;
import com.hcmute.prse_be.repository.InstructorPlatformTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;
    private final InstructorPlatformTransactionRepository instructorPlatformTransactionRepository;

    public AdminServiceImpl(AdminRepository adminRepository, InstructorPlatformTransactionRepository instructorPlatformTransactionRepository) {
        this.adminRepository = adminRepository;
        this.instructorPlatformTransactionRepository = instructorPlatformTransactionRepository;
    }

    @Override
    public AdminEntity findById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    @Override
    public AdminEntity findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    @Override
    public AdminEntity save(AdminEntity adminEntity) {
        return adminRepository.save(adminEntity);
    }

    @Override
    public Double getTotalRevenue() {
        return instructorPlatformTransactionRepository.getTotalPlatformMoney();
    }

    @Override
    public Double getTotalRevenueByMonth(int month, int year) {
        return instructorPlatformTransactionRepository.getTotalPlatformMoneyByYearAndMonth(year, month);
    }
}
