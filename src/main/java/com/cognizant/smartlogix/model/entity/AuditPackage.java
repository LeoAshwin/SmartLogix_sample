package com.cognizant.smartlogix.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Exportable audit package for regulatory or merchant audits.
 * packageUri points to a signed S3/object-storage URL of the bundled archive.
 */
@Entity
@Table(name = "audit_packages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditPackage extends BaseEntity {

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    /**
     * JSON: { "includes": ["FULFILLMENTS","POD","TRACKING","SETTLEMENTS"] }
     */
    @Column(columnDefinition = "TEXT")
    private String contentsJson;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    /** Object-storage URI of the audit archive */
    private String packageUri;
}

