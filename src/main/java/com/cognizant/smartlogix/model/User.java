package com.cognizant.smartlogix.model;

import com.cognizant.smartlogix.security.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Persistent user account.
 *
 * <p>The {@code role} field is stored as a VARCHAR using
 * {@link EnumType#STRING} so that role names remain human-readable in the DB
 * and are unaffected by enum ordinal changes.
 *
 * <p>Password authentication is handled separately via
 * {@link com.cognizant.smartlogix.security.UserPrincipal} — this entity
 * does NOT implement {@code UserDetails} to keep JPA and Security concerns
 * separated.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    /**
     * RBAC role — stored as {@code VARCHAR} (e.g. {@code "ADMIN"}).
     * Mapped from {@link Role} enum values.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private Role role;

    @Column(unique = true)
    private String email;

    private String phone;

    private String passwordHash;

    private Boolean mfaEnabled;

    private String status;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
