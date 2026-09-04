package com.uasdisprog.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Integer userId;
    private String username;
    private String role;
    private Double balance;
    private Integer customerId;
}
