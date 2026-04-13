package com.cognizant.smartlogix.dto.pricing.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CarrierBookingResponse(
        Long carrierBookingId,
        Long carrierId,
        Long fulfillmentId,
        String externalRef,
        LocalDateTime bookedAt,
        String status,
        BigDecimal feeAmount,
        String currency
) {
}