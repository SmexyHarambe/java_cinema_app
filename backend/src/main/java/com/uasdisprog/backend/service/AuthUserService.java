package com.uasdisprog.backend.service;

import com.uasdisprog.backend.dto.request.RegisterRequest;
import com.uasdisprog.backend.dto.response.LoginResponse;
import com.uasdisprog.backend.entity.User;
import com.uasdisprog.backend.repository.CustomerRepository;
import com.uasdisprog.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final AuthService authService;
    private final CustomerRepository customerRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        User user = authService.registerUser(request);

        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        // Ambil customerId + balance yang baru dibuat agar frontend langsung konsisten.
        Integer customerId = customerRepository.findByUserId(user.getId())
                .map(c -> c.getId())
                .orElse(null);
        Double balance = customerRepository.findByUserId(user.getId())
                .map(c -> c.getBalance())
                .orElse(0.0);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .balance(balance)
                .customerId(customerId)
                .build();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(String username, String password) {
        if (!authService.checkCredentials(username, password)) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = authService.findByUsername(username);

        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        Double balance = null;
        Integer customerId = null;

        if (user.getRole().name().equals("CUSTOMER")) {
            var customer = customerRepository.findByUserId(user.getId()).orElse(null);
            if (customer != null) {
                balance = customer.getBalance();
                customerId = customer.getId();
            } else {
                balance = 0.0;
            }
        }

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .balance(balance)
                .customerId(customerId)
                .build();
    }
}
