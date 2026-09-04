package com.uasdisprog.backend.repository;

import com.uasdisprog.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Optional<Ticket> findByJudul(String judul);
    boolean existsByJudul(String judul);
    
    @Query("SELECT t FROM Ticket t WHERE t.flashSaleDate = :date")
    List<Ticket> findFlashSaleTickets(@Param("date") LocalDate date);
    
    @Query("SELECT t FROM Ticket t WHERE t.stock > 0")
    List<Ticket> findAvailableTickets();
    
    @Query("SELECT t FROM Ticket t WHERE LOWER(t.judul) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Ticket> searchByKeyword(@Param("keyword") String keyword);
}
