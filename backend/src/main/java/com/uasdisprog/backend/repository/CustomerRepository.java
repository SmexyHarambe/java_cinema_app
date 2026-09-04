package com.uasdisprog.backend.repository;

import com.uasdisprog.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByUserId(Integer userId);
    Optional<Customer> findByUser_Username(String username);
}
