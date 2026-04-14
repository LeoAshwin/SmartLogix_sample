package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.CarrierStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Carrier / 3PL entity â€” contracted logistics partner.
 * JSON fields store immutable contract snapshots for dispute resolution.
 */
@Entity
@Table(name = "carriers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrier extends BaseEntity {

    @Column(nullable = false)
    private String name;

    /**
     * Immutable contract snapshot JSON for audit/dispute.
     * JSON: { "version": 1, "rateCard": {...}, "slaTerms": {...} }
     */
    @Column(columnDefinition = "TEXT")
    private String contractTermsJson;

    /** JSON array of zone IDs this carrier is allowed to serve */
    @Column(columnDefinition = "TEXT")
    private String allowedZonesJson;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CarrierStatus status;
}

