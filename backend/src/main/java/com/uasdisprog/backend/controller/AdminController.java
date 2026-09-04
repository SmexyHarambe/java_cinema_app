package com.uasdisprog.backend.controller;

import com.uasdisprog.backend.dto.request.CreateTicketRequest;
import com.uasdisprog.backend.dto.response.TicketResponse;
import com.uasdisprog.backend.dto.response.TransactionResponse;
import com.uasdisprog.backend.service.TicketService;
import com.uasdisprog.backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TicketService ticketService;
    private final TransactionService transactionService;

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestPart("ticket") CreateTicketRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        TicketResponse response = ticketService.createTicket(request, image);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tickets/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Integer id,
            @Valid @RequestPart("ticket") CreateTicketRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        TicketResponse response = ticketService.updateTicket(id, request, image);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Integer id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
