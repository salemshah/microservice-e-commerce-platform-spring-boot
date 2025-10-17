package com.ecommerce.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String action; // e.g., LOGIN, LOGOUT, PASSWORD_CHANGE

    @Column(nullable = false)
    private String description; // e.g., "User john@example.com logged in"

    @Column(nullable = false)
    private String performedBy; // email or system

    @Column(nullable = false)
    private Instant timestamp;
}
