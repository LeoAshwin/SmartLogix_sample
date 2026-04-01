package com.cognizant.smartlogix.dto.Driver.response;

import com.cognizant.smartlogix.model.data.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponse {
    private Long exceptionId;
    private Long fulfillmentId;
    private String reasonCode;
    private DeliveryStatus status;
    private int retryCount;
    private String raisedAt;
    private String suggestedAction;
}