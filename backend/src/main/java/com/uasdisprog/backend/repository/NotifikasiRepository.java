package com.uasdisprog.backend.repository;

import com.uasdisprog.backend.entity.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotifikasiRepository extends JpaRepository<Notifikasi, Integer> {
    Optional<Notifikasi> findTopByOrderByIdDesc();
}
