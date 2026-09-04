package com.uasdisprog.backend.service;

import com.uasdisprog.backend.dto.request.CreateTransactionRequest;
import com.uasdisprog.backend.dto.response.TransactionResponse;
import com.uasdisprog.backend.entity.*;
import com.uasdisprog.backend.repository.TicketRepository;
import com.uasdisprog.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TicketRepository ticketRepository;
    private final CustomerService customerService;
    private final TicketService ticketService;
    private final TcpNotificationClient tcpNotificationClient;

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByCustomerId(Integer customerId) {
        return transactionRepository.findByCustomerIdOrderByTanggalTransaksiDesc(customerId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAllByOrderByTanggalTransaksiDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponse createTransaction(Integer customerId, 
                                                CreateTransactionRequest request) {
        Customer customer = customerService.getCustomerById(customerId);
        
        if (customer.getBalance() < request.getTotal()) {
            throw new RuntimeException("Insufficient balance");
        }

        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticketService.updateStock(request.getTicketId(), request.getSeats());
        customerService.deductBalance(customerId, request.getTotal());

        // Send notification to TCP Server
        String ticketTitle = ticket.getJudul();
        String notificationMessage = "Pembelian tiket " + ticketTitle + " berhasil!";
        tcpNotificationClient.sendPersonalNotification(customerId, notificationMessage);

        String seatsBooked = String.join(",", request.getSeats());

        Transaction transaction = Transaction.builder()
                .ticket(ticket)
                .customer(customer)
                .total(request.getTotal())
                .seatsBooked(seatsBooked)
                .status(TransactionStatus.COMPLETED)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return convertToDto(saved);
    }

    @Transactional
    public void cancelTransaction(Integer transactionId, Integer customerId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (transaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel this transaction");
        }

        LocalDateTime eventDate = transaction.getTicket().getTanggalTayang();
        long daysBefore = ChronoUnit.DAYS.between(LocalDateTime.now(), eventDate);

        if (daysBefore < 7) {
            throw new RuntimeException("Cancellation only allowed 7 days before event");
        }

        customerService.refundBalance(customerId, transaction.getTotal());
        
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
    }

    private TransactionResponse convertToDto(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .ticketId(transaction.getTicket().getId())
                .ticketJudul(transaction.getTicket().getJudul())
                .customerId(transaction.getCustomer().getId())
                .customerUsername(transaction.getCustomer().getUser().getUsername())
                .tanggalTransaksi(transaction.getTanggalTransaksi())
                .total(transaction.getTotal())
                .seatsBooked(transaction.getSeatsBooked())
                .status(transaction.getStatus().name())
                .build();
    }
}
