package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.TicketEntity;

import java.util.List;

public interface TicketService {
    TicketEntity createTicket(TicketEntity ticket);

    List<TicketEntity> getAllTickets();

    TicketEntity getTicketById(Long id);

    TicketEntity updateTicket(TicketEntity ticket);
}
