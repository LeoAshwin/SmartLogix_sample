package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.ServiceZoneStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * Service zone defined by polygon GeoJSON or postal codes.
 * Holds SLA configuration and capacity per time slot.
 */
@Entity
@Table(name = "service_zones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceZone extends BaseEntity {

    @Column(nullable = false)
    private String name;

    /** GeoJSON polygon string or null if using postal codes */
    @Column(columnDefinition = "TEXT")
    private String polygonGeoJson;

    /** JSON array of postal code strings */
    @Column(columnDefinition = "TEXT")
    private String postalCodesJson;

    /**
     * JSON: { "cutOffTime": "14:00", "deliveryWindowStart": "08:00",
     *         "deliveryWindowEnd": "20:00", "fallbackZoneId": null }
     */
    @Column(columnDefinition = "TEXT")
    private String slaConfigJson;

    private Integer capacityPerSlot;

    @Column(nullable = false, length = 50)
    private String timeZone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceZoneStatus status;
}

