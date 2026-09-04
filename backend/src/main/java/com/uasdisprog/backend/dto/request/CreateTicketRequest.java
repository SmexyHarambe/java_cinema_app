package com.uasdisprog.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateTicketRequest {

    @NotBlank(message = "Judul is required")
    private String judul;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotBlank(message = "Creator is required")
    private String creator;

    @NotNull(message = "Stock is required")
    @Positive(message = "Stock must be positive")
    private Integer stock;

    private String deskripsi;

    @NotNull(message = "Durasi is required")
    @Positive(message = "Durasi must be positive")
    private Integer durasi;

    private String imagePath;

    @NotNull(message = "Tanggal tayang is required")
    private LocalDateTime tanggalTayang;

    private LocalDate flashSaleDate;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
}
