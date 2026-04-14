package com.cognizant.smartlogix.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Distribution depot / hub â€” the starting point for manifest generation.
 */
@Entity
@Table(name = "depots")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Depot extends BaseEntity {

    @Column(nullable = false)
    private String name;

    /** JSON: { "street": "...", "city": "...", "postalCode": "...", "lat": 0.0, "lng": 0.0 } */
    @Column(columnDefinition = "TEXT")
    private String addressJson;

    @Column(nullable = false, length = 50)
    private String timeZone;

    /** JSON: { "maxVehicles": 50, "maxParcelsPerDay": 2000 } */
    @Column(columnDefinition = "TEXT")
    private String capacityJson;

    @Column(nullable = false, length = 20)
    private String status;
}

