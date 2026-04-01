package com.cognizant.smartlogix.dto.Driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExceptionReportRequest {

    @NotNull(message = "Fulfillment ID is required")
    private Long fulfillmentId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotBlank(message = "Reason code cannot be empty")
    private String reasonCode; // e.g., CUSTOMER_UNAVAILABLE

    private String details;
}