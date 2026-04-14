package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.ExceptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeliveryExceptionResponse {
    private UUID id;
    private UUID fulfillmentId;
    private LocalDateTime raisedAt;
    private UUID raisedById;
    private String raisedByName;
    private String reasonCode;
    private String details;
    private String suggestedAction;
    private ExceptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

