package com.cognizant.smartlogix.dto.Driver.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PodSubmissionRequest(
        @NotNull(message = "Fulfillment ID is required")
        Long fulfillmentId,

        @NotNull(message = "Driver ID is required")
        Long driverId,

        @NotEmpty(message = "At least one photo URI is required")
        List<String> photoUris,

        @NotNull(message = "Customer signature URI is required")
        String signatureUri,

        Integer quantityDelivered,

        String notes,

        @NotNull(message = "Location is required for POD compliance")
        LocationDetails location,

        TrackingMetadata metadata
) {
    // Compact Constructor for default values
    public PodSubmissionRequest {
        if (quantityDelivered == null) {
            quantityDelivered = 1;
        }
    }
}