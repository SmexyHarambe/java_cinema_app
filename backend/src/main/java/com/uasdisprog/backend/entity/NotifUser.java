package com.uasdisprog.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notif_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notif_id", nullable = false)
    private Notifikasi notifikasi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
}
