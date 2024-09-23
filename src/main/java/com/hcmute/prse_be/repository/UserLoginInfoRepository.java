package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.UserLoginInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginInfoRepository extends JpaRepository<UserLoginInfoEntity, Long> {
}
