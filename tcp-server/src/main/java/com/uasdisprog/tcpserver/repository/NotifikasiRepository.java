package com.uasdisprog.tcpserver.repository;

import com.uasdisprog.tcpserver.entity.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotifikasiRepository extends JpaRepository<Notifikasi, Integer> {
    Optional<Notifikasi> findTopByOrderByIdDesc();
}
