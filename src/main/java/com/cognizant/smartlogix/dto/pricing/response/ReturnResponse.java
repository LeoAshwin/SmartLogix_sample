package com.cognizant.smartlogix.dto.pricing.response;

import com.cognizant.smartlogix.model.data.ReturnStatus;
import java.time.LocalDateTime;

public record ReturnResponse(
        Long returnId,
        String fulfillmentId,
        String returnLabelUri,
        LocalDateTime pickupWindowStart,
        LocalDateTime pickupWindowEnd,
        ReturnStatus status,
        LocalDateTime receivedAt,
        String inspectionResultJson
) {
}