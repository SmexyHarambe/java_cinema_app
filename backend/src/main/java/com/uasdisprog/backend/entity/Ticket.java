package com.uasdisprog.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String judul;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String creator;

    @Column(nullable = false)
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    @Column(nullable = false)
    private Integer durasi;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "tanggal_tayang", nullable = false)
    private LocalDateTime tanggalTayang;

    @Column(name = "flash_sale_date")
    private LocalDate flashSaleDate;

    @Column(nullable = false)
    private Double price;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
