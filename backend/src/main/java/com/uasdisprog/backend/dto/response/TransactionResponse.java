package com.uasdisprog.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Integer id;
    private Integer ticketId;
    private String ticketJudul;
    private Integer customerId;
    private String customerUsername;
    private LocalDateTime tanggalTransaksi;
    private Double total;
    private String seatsBooked;
    private String status;
}
