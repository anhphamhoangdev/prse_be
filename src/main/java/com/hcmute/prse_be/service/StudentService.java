package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.StudentEntity;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface StudentService {

    StudentEntity findById(Long id);

    StudentEntity findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    StudentEntity register(StudentEntity studentEntity);

    JSONObject activeAccount(String email, String activeCode);

    void sendActiveEmail(String email, String activeCode);

    void saveAvatarStudent(String urlAvatar, String username);

    long getCountStudent();

    long countByYearAndMonth(int currentYear, int currentMonth);

    Page<StudentEntity> findAllWithFilters(
            String search,
            String status,
            String role,
            int page,
            int size
    );

    StudentEntity save(StudentEntity studentEntity);
    boolean updatePassword(String currentPassword, String newPassword, String username);
}
