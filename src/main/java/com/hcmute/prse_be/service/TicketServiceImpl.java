package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.TicketEntity;
import com.hcmute.prse_be.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public TicketEntity createTicket(TicketEntity ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public List<TicketEntity> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public TicketEntity getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElse(null);
    }

    @Override
    public TicketEntity updateTicket(TicketEntity ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public long countTicketsByStatus(String status) {
        return ticketRepository.countByStatus(status);
    }
}
