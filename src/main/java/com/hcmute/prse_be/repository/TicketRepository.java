package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    List<TicketEntity> findByUserId(Long userId);
    List<TicketEntity> findByTicketType(String ticketType);
    List<TicketEntity> findByStatus(String status);
}