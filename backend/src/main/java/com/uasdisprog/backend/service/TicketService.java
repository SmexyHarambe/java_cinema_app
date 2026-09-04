package com.uasdisprog.backend.service;

import com.uasdisprog.backend.dto.response.TicketResponse;
import com.uasdisprog.backend.entity.Ticket;
import com.uasdisprog.backend.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Integer id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return convertToDto(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getFlashSaleTickets() {
        return ticketRepository.findFlashSaleTickets(LocalDate.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> searchTickets(String keyword) {
        return ticketRepository.searchByKeyword(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponse createTicket(com.uasdisprog.backend.dto.request.CreateTicketRequest request,
                                       MultipartFile imageFile) throws IOException {
        String imagePath = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imagePath = saveImage(imageFile);
        }

        Ticket ticket = Ticket.builder()
                .judul(request.getJudul())
                .genre(request.getGenre())
                .creator(request.getCreator())
                .stock(request.getStock())
                .deskripsi(request.getDeskripsi())
                .durasi(request.getDurasi())
                .imagePath(imagePath)
                .tanggalTayang(request.getTanggalTayang())
                .flashSaleDate(request.getFlashSaleDate())
                .price(request.getPrice())
                .build();

        Ticket saved = ticketRepository.save(ticket);
        return convertToDto(saved);
    }

    @Transactional
    public TicketResponse updateTicket(Integer id, 
                                       com.uasdisprog.backend.dto.request.CreateTicketRequest request,
                                       MultipartFile imageFile) throws IOException {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setJudul(request.getJudul());
        ticket.setGenre(request.getGenre());
        ticket.setCreator(request.getCreator());
        ticket.setStock(request.getStock());
        ticket.setDeskripsi(request.getDeskripsi());
        ticket.setDurasi(request.getDurasi());
        ticket.setTanggalTayang(request.getTanggalTayang());
        ticket.setFlashSaleDate(request.getFlashSaleDate());
        ticket.setPrice(request.getPrice());

        if (imageFile != null && !imageFile.isEmpty()) {
            if (ticket.getImagePath() != null) {
                deleteOldImage(ticket.getImagePath());
            }
            ticket.setImagePath(saveImage(imageFile));
        }

        Ticket updated = ticketRepository.save(ticket);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteTicket(Integer id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (ticket.getImagePath() != null) {
            deleteOldImage(ticket.getImagePath());
        }
        
        ticketRepository.delete(ticket);
    }

    @Transactional
    public void updateStock(Integer ticketId, List<String> seats) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        int bookedCount = seats.size();
        if (ticket.getStock() < bookedCount) {
            throw new RuntimeException("Not enough stock");
        }
        
        ticket.setStock(ticket.getStock() - bookedCount);
        ticketRepository.save(ticket);
    }

    private String saveImage(MultipartFile file) throws IOException {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath);

        return filename;
    }

    private void deleteOldImage(String imagePath) {
        try {
            Path path = Paths.get(uploadDir, imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't throw
        }
    }

    private TicketResponse convertToDto(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .judul(ticket.getJudul())
                .genre(ticket.getGenre())
                .creator(ticket.getCreator())
                .stock(ticket.getStock())
                .deskripsi(ticket.getDeskripsi())
                .durasi(ticket.getDurasi())
                .imagePath(ticket.getImagePath())
                .tanggalTayang(ticket.getTanggalTayang())
                .flashSaleDate(ticket.getFlashSaleDate())
                .price(ticket.getPrice())
                .createdAt(ticket.getCreatedAt())
                .isFlashSale(ticket.getFlashSaleDate() != null)
                .build();
    }
}
