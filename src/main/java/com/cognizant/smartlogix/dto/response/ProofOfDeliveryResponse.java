package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.PODStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProofOfDeliveryResponse {
    private UUID id;
    private UUID fulfillmentId;
    private LocalDateTime deliveredAt;
    private UUID deliveredById;
    private String deliveredByName;
    private String photoUrisJson;
    private String signatureUri;
    private Integer quantityDelivered;
    private String notes;
    private PODStatus status;
    private LocalDateTime createdAt;
}

