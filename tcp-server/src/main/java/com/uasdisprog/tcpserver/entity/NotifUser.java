package com.uasdisprog.tcpserver.entity;

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

    @Column(name = "notif_id", nullable = false)
    private Integer notifId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
}
