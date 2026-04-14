package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreateCarrierBookingRequest {

    @NotNull(message = "Carrier ID is required")
    private UUID carrierId;

    @NotNull(message = "Fulfillment ID is required")
    private UUID fulfillmentId;

    private String externalRef;

    @NotNull(message = "Fee amount is required")
    @Positive
    private BigDecimal feeAmount;

    @NotNull(message = "Currency is required")
    private String currency;
}

