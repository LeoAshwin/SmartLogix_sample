package com.cognizant.smartlogix.dto.pricing;

import java.time.LocalDateTime;

public record ReturnCreateRequest(
        Long fulfillmentId,
        LocalDateTime pickupWindowStart,
        LocalDateTime pickupWindowEnd
) {
}