package com.uasdisprog.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private Integer id;
    private String message;
    private String type;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
