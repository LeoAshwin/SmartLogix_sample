package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RaiseDeliveryExceptionRequest {

    @NotNull(message = "Fulfillment ID is required")
    private UUID fulfillmentId;

    @NotNull(message = "Raised by (user ID) is required")
    private UUID raisedById;

    @NotBlank(message = "Reason code is required")
    private String reasonCode;

    private String details;

    private String suggestedAction;
}

