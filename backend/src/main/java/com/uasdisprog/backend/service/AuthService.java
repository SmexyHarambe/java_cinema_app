package com.uasdisprog.backend.service;

import com.uasdisprog.backend.entity.Customer;
import com.uasdisprog.backend.entity.User;
import com.uasdisprog.backend.entity.UserRole;
import com.uasdisprog.backend.repository.CustomerRepository;
import com.uasdisprog.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(com.uasdisprog.backend.dto.request.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullname(request.getFullname())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .memberSince(request.getMemberSince())
                .role(UserRole.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);

        Customer customer = Customer.builder()
                .user(savedUser)
                .balance(0.0)
                .build();

        customerRepository.save(customer);

        return savedUser;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public boolean checkCredentials(String username, String password) {
        User user = findByUsername(username);
        return passwordEncoder.matches(password, user.getPassword());
    }
}
