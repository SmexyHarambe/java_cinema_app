package com.uasdisprog.backend.repository;

import com.uasdisprog.backend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByUserId(Integer userId);
    Optional<Admin> findByUser_Username(String username);
}
