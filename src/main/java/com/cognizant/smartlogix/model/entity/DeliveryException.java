package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.ExceptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Delivery exception task â€” created when a delivery attempt fails.
 * Renamed from "Exception" to avoid conflict with java.lang.Exception.
 */
@Entity
@Table(name = "delivery_exceptions", indexes = {
        @Index(name = "idx_exceptions_fulfillment", columnList = "fulfillment_id"),
        @Index(name = "idx_exceptions_status",      columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryException extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fulfillment_id", nullable = false)
    private Fulfillment fulfillment;

    @Column(nullable = false)
    private LocalDateTime raisedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raised_by", nullable = false)
    private User raisedBy;

    /**
     * Standardized reason code, e.g. "CUSTOMER_ABSENT", "ADDRESS_NOT_FOUND",
     * "ACCESS_DENIED", "REFUSED_DELIVERY"
     */
    @Column(nullable = false, length = 50)
    private String reasonCode;

    @Column(columnDefinition = "TEXT")
    private String details;

    /**
     * Deterministic suggested action, e.g. "REATTEMPT_NEXT_DAY", "RETURN_TO_HUB"
     */
    @Column(length = 50)
    private String suggestedAction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExceptionStatus status;
}

