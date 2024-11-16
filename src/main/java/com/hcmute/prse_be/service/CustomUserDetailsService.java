package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.StudentEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {
    StudentEntity findByUsername(String username);
}
