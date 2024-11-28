package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.StudentEntity;
import net.minidev.json.JSONObject;

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

}
