package com.uasdisprog.backend.controller;

import com.uasdisprog.backend.dto.response.TicketResponse;
import com.uasdisprog.backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/tickets")
@RequiredArgsConstructor
public class PublicTicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Integer id) {
        TicketResponse ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/flash-sale")
    public ResponseEntity<List<TicketResponse>> getFlashSaleTickets() {
        List<TicketResponse> tickets = ticketService.getFlashSaleTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TicketResponse>> searchTickets(@RequestParam String keyword) {
        List<TicketResponse> tickets = ticketService.searchTickets(keyword);
        return ResponseEntity.ok(tickets);
    }
}
