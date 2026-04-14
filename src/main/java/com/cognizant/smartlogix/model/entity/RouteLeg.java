package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.RouteLegStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Individual route leg within a manifest â€” from one location to the next.
 */
@Entity
@Table(name = "route_legs", indexes = {
        @Index(name = "idx_route_legs_manifest", columnList = "manifest_id,sequence")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteLeg extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manifest_id", nullable = false)
    private Manifest manifest;

    @Column(nullable = false)
    private Integer sequence;

    /**
     * JSON: { "lat": 12.9716, "lng": 77.5946, "address": "..." }
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String fromLocationJson;

    /**
     * JSON: { "lat": 12.9716, "lng": 77.5946, "address": "..." }
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String toLocationJson;

    @Column(precision = 10, scale = 2)
    private BigDecimal distanceKm;

    private Integer estimatedDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RouteLegStatus status;
}

