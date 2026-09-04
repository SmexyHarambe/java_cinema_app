package com.uasdisprog.backend.service;

import com.uasdisprog.backend.dto.response.NotificationMessage;
import com.uasdisprog.backend.entity.Customer;
import com.uasdisprog.backend.entity.Notifikasi;
import com.uasdisprog.backend.entity.NotifUser;
import com.uasdisprog.backend.entity.NotificationType;
import com.uasdisprog.backend.repository.CustomerRepository;
import com.uasdisprog.backend.repository.NotifikasiRepository;
import com.uasdisprog.backend.repository.NotifUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotifikasiRepository notifikasiRepository;
    private final NotifUserRepository notifUserRepository;
    private final CustomerRepository customerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void broadcastNotification(String message) {
        Notifikasi notif = Notifikasi.builder()
                .message(message)
                .type(NotificationType.BROADCAST)
                .build();
        Notifikasi saved = notifikasiRepository.save(notif);

        NotifUser notifUser = NotifUser.builder()
                .notifikasi(saved)
                .isRead(false)
                .build();
        notifUserRepository.save(notifUser);

        messagingTemplate.convertAndSend("/topic/notifications", convertToDto(saved));
        log.info("Broadcast notification: {}", message);
    }

    @Transactional
    public void sendPersonalNotification(Integer customerId, String message) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Notifikasi notif = Notifikasi.builder()
                .message(message)
                .type(NotificationType.PERSONAL)
                .build();
        Notifikasi saved = notifikasiRepository.save(notif);

        NotifUser notifUser = NotifUser.builder()
                .notifikasi(saved)
                .customer(customer)
                .isRead(false)
                .build();
        notifUserRepository.save(notifUser);

        messagingTemplate.convertAndSend("/queue/user/" + customerId, convertToDto(saved));
        log.info("Personal notification sent to customer {}: {}", customerId, message);
    }

    @Transactional(readOnly = true)
    public List<NotificationMessage> getUnreadNotifications(Integer customerId) {
        return notifUserRepository.findUnreadByCustomerId(customerId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Integer notifUserId) {
        NotifUser notifUser = notifUserRepository.findById(notifUserId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notifUser.setIsRead(true);
        notifUserRepository.save(notifUser);
    }

    @Transactional
    public void deleteNotification(Integer notifUserId) {
        notifUserRepository.deleteById(notifUserId);
    }

    private NotificationMessage convertToDto(Notifikasi notifikasi) {
        return NotificationMessage.builder()
                .id(notifikasi.getId())
                .message(notifikasi.getMessage())
                .type(notifikasi.getType().name())
                .createdAt(notifikasi.getCreatedAt())
                .isRead(true)
                .build();
    }

    private NotificationMessage convertToDto(NotifUser notifUser) {
        return NotificationMessage.builder()
                .id(notifUser.getId())
                .message(notifUser.getNotifikasi().getMessage())
                .type(notifUser.getNotifikasi().getType().name())
                .createdAt(notifUser.getNotifikasi().getCreatedAt())
                .isRead(notifUser.getIsRead())
                .build();
    }
}
