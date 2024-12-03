package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.AdminEntity;

public interface AdminService {
    AdminEntity findById(Long id);
    AdminEntity findByEmail(String email);
    boolean existsByEmail(String email);
    AdminEntity save(AdminEntity adminEntity);

}
