package com.cognizant.smartlogix.dto.pricing.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CarrierBookingResponse(
        Long carrierBookingId,
        String carrierId,
        String fulfillmentId,
        String externalRef,
        LocalDateTime bookedAt,
        String status,
        BigDecimal feeAmount,
        String currency
) {
}