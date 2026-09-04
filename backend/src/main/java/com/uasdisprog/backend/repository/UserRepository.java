package com.uasdisprog.backend.repository;

import com.uasdisprog.backend.entity.User;
import com.uasdisprog.backend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsernameAndRole(String username, UserRole role);
}
