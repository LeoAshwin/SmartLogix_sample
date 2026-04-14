package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.CarrierBookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Carrier booking â€” links a fulfillment to a contracted carrier.
 */
@Entity
@Table(name = "carrier_bookings", indexes = {
        @Index(name = "idx_carrier_bookings_fulfillment", columnList = "fulfillment_id"),
        @Index(name = "idx_carrier_bookings_carrier",     columnList = "carrier_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierBooking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fulfillment_id", nullable = false)
    private Fulfillment fulfillment;

    /** Carrier's own booking / tracking reference */
    @Column(length = 100)
    private String externalRef;

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CarrierBookingStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal feeAmount;

    @Column(nullable = false, length = 10)
    private String currency;
}

