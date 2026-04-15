package com.cognizant.smartlogix.dto.Manager;

import java.time.LocalDateTime;

public record FulfillmentResponse(
        String fulfillmentId,
        String status,
        LocalDateTime createdAt
) {
}