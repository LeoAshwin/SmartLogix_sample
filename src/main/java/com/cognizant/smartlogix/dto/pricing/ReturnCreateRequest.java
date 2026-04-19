package com.cognizant.smartlogix.dto.pricing;

import java.time.LocalDateTime;

public record ReturnCreateRequest(
        String fulfillmentId,
        LocalDateTime pickupWindowStart,
        LocalDateTime pickupWindowEnd
) {
}