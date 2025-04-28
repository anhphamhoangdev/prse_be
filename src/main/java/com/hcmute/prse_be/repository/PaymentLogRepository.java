package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.PaymentLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentLogRepository extends JpaRepository<PaymentLogEntity, Long>{
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentLogEntity p WHERE p.studentId = :studentId")
    Double sumAmountByStudentId(@Param("studentId") Long studentId);
}
