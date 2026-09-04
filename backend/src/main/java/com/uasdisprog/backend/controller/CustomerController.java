package com.uasdisprog.backend.controller;

import com.uasdisprog.backend.dto.request.TopupRequest;
import com.uasdisprog.backend.dto.request.CreateTransactionRequest;
import com.uasdisprog.backend.dto.response.TransactionResponse;
import com.uasdisprog.backend.entity.Customer;
import com.uasdisprog.backend.security.UserPrincipal;
import com.uasdisprog.backend.service.CustomerService;
import com.uasdisprog.backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final TransactionService transactionService;

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(@AuthenticationPrincipal UserPrincipal principal) {
        Customer customer = customerService.getCustomerByUsername(principal.getUsername());
        return ResponseEntity.ok(customer.getBalance());
    }

    @PostMapping("/topup")
    public ResponseEntity<Double> topupBalance(@AuthenticationPrincipal UserPrincipal principal,
                                               @Valid @RequestBody TopupRequest request) {
        Customer customer = customerService.getCustomerByUsername(principal.getUsername());
        Customer updated = customerService.topupBalance(customer.getId(), request.getAmount());
        return ResponseEntity.ok(updated.getBalance());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @AuthenticationPrincipal UserPrincipal principal) {
        Customer customer = customerService.getCustomerByUsername(principal.getUsername());
        List<TransactionResponse> transactions = transactionService.getTransactionsByCustomerId(customer.getId());
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateTransactionRequest request) {
        Customer customer = customerService.getCustomerByUsername(principal.getUsername());
        TransactionResponse response = transactionService.createTransaction(customer.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> cancelTransaction(@AuthenticationPrincipal UserPrincipal principal,
                                                  @PathVariable Integer id) {
        Customer customer = customerService.getCustomerByUsername(principal.getUsername());
        transactionService.cancelTransaction(id, customer.getId());
        return ResponseEntity.noContent().build();
    }
}
