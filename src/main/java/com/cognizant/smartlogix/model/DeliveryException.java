package com.cognizant.smartlogix.model;
import com.cognizant.smartlogix.model.data.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "delivery_exception")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryException {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // automatically generates id's'
    private Long exceptionId;

    @Column(nullable = false)
    private Long fulfillmentId;

    private LocalDateTime raisedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Long raisedBy; // Driver who reported the failure

    @Column(nullable = false)
    private String reasonCode; // e.g., CUSTOMER_UNAVAILABLE, DAMAGED

    @Column(columnDefinition = "TEXT")
    private String details;

    private String suggestedAction; // e.g., REATTEMPT, RETURN_TO_HUB

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status= DeliveryStatus.OPEN; // [Open/Resolved/Escalated]

    private Integer retryCount = 0; // To support reattempt rules
}