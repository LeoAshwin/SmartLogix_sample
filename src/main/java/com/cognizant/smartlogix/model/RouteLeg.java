package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "RouteLeg") // Strictly matching doc
@Data
public class RouteLeg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LegID")
    private Long legId;

    @Column(name = "ManifestID")
    private Long manifestId;

    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "FromLocationJSON", columnDefinition = "JSON")
    private String fromLocationJson;

    @Column(name = "ToLocationJSON", columnDefinition = "JSON")
    private String toLocationJson;

    @Column(name = "DistanceKm", columnDefinition = "DECIMAL(10,2)")
    private Double distanceKm;

    @Column(name = "EstimatedDurationMinutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "Status")
    private String status;
}