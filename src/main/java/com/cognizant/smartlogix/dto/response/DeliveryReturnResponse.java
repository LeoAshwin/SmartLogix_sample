package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.ReturnStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeliveryReturnResponse {
    private UUID id;
    private UUID fulfillmentId;
    private String returnLabelUri;
    private LocalDateTime pickupWindowStart;
    private LocalDateTime pickupWindowEnd;
    private ReturnStatus status;
    private LocalDateTime receivedAt;
    private String inspectionResultJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

