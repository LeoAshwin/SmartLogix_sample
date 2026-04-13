package com.cognizant.smartlogix.dto.pricing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CarrierBookingCreateRequest(
        Long carrierId,
        Long fulfillmentId,
        String externalRef,
        LocalDateTime bookedAt,
        String status,
        BigDecimal feeAmount,
        String currency
) {
}