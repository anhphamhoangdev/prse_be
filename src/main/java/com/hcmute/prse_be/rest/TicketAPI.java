package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.UserType;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.entity.TicketEntity;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.InstructorService;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.StudentService;
import com.hcmute.prse_be.service.TicketService;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/ticket")
public class TicketAPI {

    // Define your endpoints here
    // For example:
    // @GetMapping
    // public List<TicketEntity> getAllTickets() {
    //     return ticketService.getAllTickets();
    // }

    // @PostMapping
    // public TicketEntity createTicket(@RequestBody TicketEntity ticket) {
    //     return ticketService.createTicket(ticket);
    // }

    private final TicketService ticketService;

    private final StudentService studentService;
    private final InstructorService instructorService;

    public TicketAPI(TicketService ticketService, StudentService studentService, InstructorService instructorService) {
        this.ticketService = ticketService;
        this.studentService = studentService;
        this.instructorService = instructorService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTicket( @RequestBody TicketEntity ticket, Authentication authentication) {
        LogService.getgI().info("[TicketAPI] createTicket by: " + authentication.getName());

        try {
            // Check if user is authenticated
            if (!authentication.isAuthenticated()) {
                return ResponseEntity.badRequest().body(Response.error("User not authenticated"));
            }

            // Verify user exists
            String username = authentication.getName();
            StudentEntity studentAccount = studentService.findByUsername(username);

            // tuong lai se co instructor
            if (studentAccount == null) {
                return ResponseEntity.badRequest().body(Response.error("User does not exist"));
            }


            // Set user details in ticket
            ticket.setUserId(studentAccount.getId());
            ticket.setUserType(ticket.getUserType());


            if(Objects.equals(ticket.getUserType(), UserType.INSTRUCTOR)){
                InstructorEntity instructorAccount = instructorService.getInstructorByStudentId(studentAccount.getId());
                if (instructorAccount == null) {
                    return ResponseEntity.badRequest().body(Response.error("User does not exist"));
                }
                ticket.setUserId(instructorAccount.getId());
            }

            // Additional validation based on ticket type
            if ("payment".equals(ticket.getTicketType()) && ticket.getPaymentLogId() == null) {
                return ResponseEntity.badRequest().body(Response.error("Payment log ID is required for payment tickets"));
            }
            if ("course".equals(ticket.getTicketType()) && ticket.getCourseId() == null) {
                return ResponseEntity.badRequest().body(Response.error("Course ID is required for course tickets"));
            }

            // Create ticket
            TicketEntity createdTicket = ticketService.createTicket(ticket);
            JSONObject response = new JSONObject();
            response.put("ticket", createdTicket);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Response.error("Failed to create ticket: " + e.getMessage()));
        }
    }
}
