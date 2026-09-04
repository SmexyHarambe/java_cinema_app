package com.uasdisprog.backend.controller;

import com.uasdisprog.backend.dto.response.NotificationMessage;
import com.uasdisprog.backend.security.UserPrincipal;
import com.uasdisprog.backend.service.CustomerService;
import com.uasdisprog.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CustomerService customerService;

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationMessage>> getUnreadNotifications(
            @AuthenticationPrincipal UserPrincipal principal) {
        var customer = customerService.getCustomerByUsername(principal.getUsername());
        List<NotificationMessage> notifications = notificationService.getUnreadNotifications(customer.getId());
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/notification")
    @SendTo("/topic/notifications")
    public NotificationMessage sendNotification(NotificationMessage message) {
        return message;
    }
}
