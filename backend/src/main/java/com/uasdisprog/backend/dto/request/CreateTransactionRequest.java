package com.uasdisprog.backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    @NotNull(message = "Ticket ID is required")
    private Integer ticketId;

    @NotEmpty(message = "Seats must not be empty")
    private List<String> seats;

    @NotNull(message = "Total is required")
    @Positive(message = "Total must be positive")
    private Double total;
}
