package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CaptureProofOfDeliveryRequest {

    @NotNull(message = "Fulfillment ID is required")
    private UUID fulfillmentId;

    @NotNull(message = "Delivery timestamp is required")
    private LocalDateTime deliveredAt;

    @NotNull(message = "Delivered by (user ID) is required")
    private UUID deliveredById;

    private String photoUrisJson;

    private String signatureUri;

    private String signatureSha256;

    @NotNull(message = "Quantity is required")
    private Integer quantityDelivered;

    private String notes;
}

