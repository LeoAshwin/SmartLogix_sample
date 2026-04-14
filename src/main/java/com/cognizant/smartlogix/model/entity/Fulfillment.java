package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import com.cognizant.smartlogix.model.enums.ServiceLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Core order unit â€” the primary aggregate in SmartLogix.
 *
 * @Version enables optimistic locking to prevent double-assignment of a
 * fulfillment to two manifests concurrently.
 */
@Entity
@Table(name = "fulfillments", indexes = {
        @Index(name = "idx_fulfillments_order_id",     columnList = "orderId"),
        @Index(name = "idx_fulfillments_zone_window",  columnList = "serviceZone_id,deliveryWindowStart"),
        @Index(name = "idx_fulfillments_status",       columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fulfillment extends BaseEntity {

    /** Idempotency key â€” caller-supplied order reference */
    @Column(nullable = false, unique = true, length = 100)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_zone_id", nullable = false)
    private ServiceZone serviceZone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceLevel serviceLevel;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal packageWeightKg;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal packageVolumeM3;

    /**
     * JSON: { "lengthCm": 30, "widthCm": 20, "heightCm": 15 }
     */
    @Column(columnDefinition = "TEXT")
    private String dimensionsJson;

    @Column(nullable = false)
    private LocalDateTime deliveryWindowStart;

    @Column(nullable = false)
    private LocalDateTime deliveryWindowEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FulfillmentStatus status;

    /** Original raw payload retained for audit */
    @Column(columnDefinition = "TEXT")
    private String originalPayload;

    /** Geocoded and normalized address */
    @Column(columnDefinition = "TEXT")
    private String normalizedAddressJson;

    @Version
    private Long version;
}

