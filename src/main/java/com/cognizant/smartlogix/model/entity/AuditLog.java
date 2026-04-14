package com.cognizant.smartlogix.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Append-only audit log â€” records every state-changing action.
 * Must NEVER be updated or deleted (enforced at application layer).
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_user_ts",    columnList = "userId,timestamp"),
        @Index(name = "idx_audit_logs_resource",   columnList = "resourceType,resourceId")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

    /** Nullable for system-generated events */
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(nullable = false, length = 50)
    private String resourceType;

    @Column(nullable = false)
    private UUID resourceId;

    /**
     * JSON: before/after state snapshot, IP address, etc.
     */
    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}

