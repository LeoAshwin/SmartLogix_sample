package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.UserRole;
import com.cognizant.smartlogix.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * Platform user â€” covers all roles: LOGISTICS_MANAGER, DISPATCHER, DRIVER,
 * CUSTOMER, MERCHANT, CARRIER, FINANCE_OFFICER, ADMINISTRATOR.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    @Builder.Default
    private Boolean mfaEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;
}

