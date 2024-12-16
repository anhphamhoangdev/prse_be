package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<BankEntity, Long> {

    List<BankEntity> findAllByOrderByOrderIndexAsc();
}
