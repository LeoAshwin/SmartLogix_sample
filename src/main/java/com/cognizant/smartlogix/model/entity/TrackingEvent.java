package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.TrackingEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable tracking event â€” append-only history of a fulfillment's journey.
 * Never update or delete records; only insert new events.
 */
@Entity
@Table(name = "tracking_events", indexes = {
        @Index(name = "idx_tracking_events_fulfillment_ts",
               columnList = "fulfillment_id,timestamp")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fulfillment_id", nullable = false)
    private Fulfillment fulfillment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TrackingEventType eventType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * JSON: { "lat": 12.9716, "lng": 77.5946 }
     */
    @Column(columnDefinition = "TEXT")
    private String locationJson;

    /**
     * JSON: { "driverId": "...", "note": "...", "attemptNumber": 1 }
     */
    @Column(columnDefinition = "TEXT")
    private String detailsJson;
}

