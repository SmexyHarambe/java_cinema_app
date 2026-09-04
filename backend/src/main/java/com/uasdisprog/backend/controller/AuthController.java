package com.uasdisprog.backend.controller;

import com.uasdisprog.backend.dto.request.LoginRequest;
import com.uasdisprog.backend.dto.request.RegisterRequest;
import com.uasdisprog.backend.dto.response.LoginResponse;
import com.uasdisprog.backend.service.AuthUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authUserService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authUserService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }
}
