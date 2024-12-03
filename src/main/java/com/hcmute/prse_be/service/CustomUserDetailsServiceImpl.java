package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.entity.AdminEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.repository.AdminRepository;
import com.hcmute.prse_be.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService{

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public CustomUserDetailsServiceImpl(StudentRepository studentRepository, AdminRepository adminRepository) {
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
    }


    @Override
    public StudentEntity findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check Student first
        StudentEntity student = studentRepository.findByUsername(username);
        if(student != null) {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("STUDENT"));
            if(student.isInstructor()) {
                authorities.add(new SimpleGrantedAuthority("INSTRUCTOR"));
            }
            return new User(student.getUsername(), student.getPasswordHash(), authorities);
        }

        AdminEntity admin = adminRepository.findByEmail(username);
        if(admin != null) {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
            return new User(admin.getEmail(), admin.getPasswordHash(), authorities);
        }

        // If not found in both tables
        throw new UsernameNotFoundException(ErrorMsg.STUDENT_USERNAME_NOT_EXIST);
    }
}
