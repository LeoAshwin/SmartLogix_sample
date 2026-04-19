package com.cognizant.smartlogix.dto.Driver.response;

import com.cognizant.smartlogix.model.data.DeliveryStatus;
import lombok.Builder;

@Builder
public record ExceptionResponse(
        Long exceptionId,
        String fulfillmentId,
        String reasonCode,
        DeliveryStatus status,
        int retryCount,
        String raisedAt,
        String suggestedAction
) {

}