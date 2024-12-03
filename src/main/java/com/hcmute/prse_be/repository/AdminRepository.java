package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    boolean existsByEmail(String email);
    // find by email
    AdminEntity findByEmail(String email);
}
