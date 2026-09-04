package com.uasdisprog.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Integer id;
    private String judul;
    private String genre;
    private String creator;
    private Integer stock;
    private String deskripsi;
    private Integer durasi;
    private String imagePath;
    private LocalDateTime tanggalTayang;
    private LocalDate flashSaleDate;
    private Double price;
    private LocalDateTime createdAt;
    private Boolean isFlashSale;
}
