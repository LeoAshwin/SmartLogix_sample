package com.cognizant.smartlogix.model.entity;

import com.cognizant.smartlogix.model.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Reverse logistics return â€” linked to original fulfillment.
 * Table named "delivery_returns" to avoid conflict with SQL reserved word.
 */
@Entity
@Table(name = "delivery_returns", indexes = {
        @Index(name = "idx_returns_fulfillment", columnList = "fulfillment_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReturn extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fulfillment_id", nullable = false)
    private Fulfillment fulfillment;

    /** Object-storage URI for the pre-printed return label */
    private String returnLabelUri;

    private LocalDateTime pickupWindowStart;

    private LocalDateTime pickupWindowEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private ReturnStatus status;

    private LocalDateTime receivedAt;

    /**
     * JSON: { "condition": "DAMAGED", "disposition": "SCRAP", "notes": "..." }
     */
    @Column(columnDefinition = "TEXT")
    private String inspectionResultJson;
}

