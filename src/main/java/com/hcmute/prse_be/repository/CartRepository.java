package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {

    Optional<CartEntity> findByStudentId(Long studentId);

}
