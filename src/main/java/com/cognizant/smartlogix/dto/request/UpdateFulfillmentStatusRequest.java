package com.cognizant.smartlogix.dto.request;

import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFulfillmentStatusRequest {

    @NotNull(message = "Status is required")
    private FulfillmentStatus status;

    private String reason;
}

