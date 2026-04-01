package com.cognizant.smartlogix.dto.Driver;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PodSubmissionRequest {
    @NotNull(message = "Fulfillment ID is required")
    private Long fulfillmentId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotEmpty(message = "At least one photo URI is required")
    private List<String> photoUris;

    @NotNull(message = "Customer signature URI is required")
    private String signatureUri;

    private Integer quantityDelivered = 1;
    private String notes;

    @NotNull(message = "Location is required for POD compliance")
    private LocationDetails location;

    private TrackingMetadata metadata;
}