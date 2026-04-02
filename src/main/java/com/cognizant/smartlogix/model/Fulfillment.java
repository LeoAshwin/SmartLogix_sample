package com.cognizant.smartlogix.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity representing a Fulfillment record.
 *
 * <p>
 * Stores delivery-related information created during
 * order ingestion and validation.
 * </p>
 *
 * @module 4.1 Order Ingestion & Validation
 */
@Entity
@Data
@Table(name = "fulfillment")
public class Fulfillment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fulfillmentId;

    private Long orderId;
    private Long merchantId;
    private Long serviceZoneId;

    private String serviceLevel;

    private Double packageWeightKg;
    private Double packageVolumeM3;

    @Column(columnDefinition = "TEXT")
    private String dimensionsJson;

    private LocalDateTime deliveryWindowStart;
    private LocalDateTime deliveryWindowEnd;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}