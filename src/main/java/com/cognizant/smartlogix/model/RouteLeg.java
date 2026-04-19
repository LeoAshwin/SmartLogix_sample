package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`RouteLeg`")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteLeg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`LegID`")
    private Long legId;

    @Column(name = "`ManifestID`")
    private Long manifestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`ManifestID`", referencedColumnName = "`ManifestID`", insertable = false, updatable = false)
    private Manifest manifest;

    @Column(name = "`Sequence`")
    private Integer sequence;

    @Column(name = "`FromLocationJSON`", columnDefinition = "JSON")
    private String fromLocationJson;

    @Column(name = "`ToLocationJSON`", columnDefinition = "JSON")
    private String toLocationJson;

    @Column(name = "`DistanceKm`", columnDefinition = "DECIMAL(10,2)")
    private Double distanceKm;

    @Column(name = "`EstimatedDurationMinutes`")
    private Integer estimatedDurationMinutes;

    @Column(name = "`Status`")
    private String status;
}