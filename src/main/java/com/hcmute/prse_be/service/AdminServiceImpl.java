package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.AdminEntity;
import com.hcmute.prse_be.repository.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
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
}
