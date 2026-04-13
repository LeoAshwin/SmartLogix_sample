package com.cognizant.smartlogix.dto.Driver.response;

import java.time.LocalDateTime;

/**
 * Response DTO for fulfillment creation.
 */
public record FulfillmentResponse(
        String fulfillmentId,
        String status,
        LocalDateTime createdAt
) {
}