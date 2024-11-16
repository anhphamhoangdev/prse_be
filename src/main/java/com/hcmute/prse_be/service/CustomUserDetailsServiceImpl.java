package com.hcmute.prse_be.service;

import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.entity.StudentEntity;
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

    @Autowired
    public CustomUserDetailsServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @Override
    public StudentEntity findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        StudentEntity student = findByUsername(username);

        if(student == null)
        {
            throw new UsernameNotFoundException(ErrorMsg.STUDENT_USERNAME_NOT_EXIST);
        }

        // set tam (bua sau neu co trong instructor se gan them quyen vao)
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("STUDENT"));

        User user = new User(student.getUsername(), student.getPasswordHash(), authorities);

        return user;
    }
}
