package com.cognizant.smartlogix.dto.Driver.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExceptionReportRequest(
        @NotNull(message = "Fulfillment ID is required")
        String fulfillmentId,

        @NotNull(message = "Driver ID is required")
        String driverId,

        @NotBlank(message = "Reason code cannot be empty")
        String reasonCode,

        String details
) {

}