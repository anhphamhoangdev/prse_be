package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.WithDrawEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WithdrawRepository extends JpaRepository<WithDrawEntity, Long> {

    List<WithDrawEntity> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);

    @Query("SELECT SUM(w.amount) FROM WithDrawEntity w WHERE w.status = :status")
    Double sumAmountByStatus(String status);

    List<WithDrawEntity> findAllByInstructorIdOrderByIdDesc(Long instructorId);



}
