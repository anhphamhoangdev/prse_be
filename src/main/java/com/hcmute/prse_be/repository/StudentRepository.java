package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    StudentEntity findByUsername(String username);
    StudentEntity findByEmail(String email);
    StudentEntity findByPhoneNumber(String phone);
}
