package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.ManifestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Delivery manifest â€” groups ordered stops for a driver/vehicle on a date.
 * stopsJson holds deterministic stop sequence with ETAs.
 */
@Entity
@Table(name = "manifests", indexes = {
        @Index(name = "idx_manifests_date_depot", columnList = "depot_id,date"),
        @Index(name = "idx_manifests_driver",      columnList = "driver_id"),
        @Index(name = "idx_manifests_vehicle",     columnList = "vehicle_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Manifest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "depot_id", nullable = false)
    private Depot depot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(nullable = false)
    private LocalDate date;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    /**
     * JSON array: [{"fulfillmentId":"...","sequence":1,"etaWindowStart":"...","etaWindowEnd":"..."}]
     */
    @Column(columnDefinition = "TEXT")
    private String stopsJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManifestStatus status;
}

