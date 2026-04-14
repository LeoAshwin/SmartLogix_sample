package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InitiateReturnRequest {

    @NotNull(message = "Fulfillment ID is required")
    private UUID fulfillmentId;

    private String returnLabelUri;

    private LocalDateTime pickupWindowStart;

    private LocalDateTime pickupWindowEnd;
}

