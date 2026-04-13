package com.cognizant.smartlogix.dto.Manager;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a fulfillment.
 * Implemented as a Java record (immutable).
 */
public record FulfillmentRequest(
        String orderId,
        String merchantId,
        String serviceZoneId,
        String serviceLevel,
        Double packageWeightKg,
        Double packageVolumeM3,
        String dimensionsJson,
        LocalDateTime deliveryWindowStart,
        LocalDateTime deliveryWindowEnd
) {
}