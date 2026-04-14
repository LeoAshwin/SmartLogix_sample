package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.TrackingEventType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrackingEventResponse {
    private UUID id;
    private UUID fulfillmentId;
    private TrackingEventType eventType;
    private LocalDateTime timestamp;
    private String locationJson;
    private String detailsJson;
    private LocalDateTime createdAt;
}

