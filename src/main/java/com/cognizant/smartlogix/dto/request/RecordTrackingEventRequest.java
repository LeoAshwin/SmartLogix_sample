package com.cognizant.smartlogix.dto.request;

import com.cognizant.smartlogix.model.enums.TrackingEventType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RecordTrackingEventRequest {

    @NotNull(message = "Fulfillment ID is required")
    private UUID fulfillmentId;

    @NotNull(message = "Event type is required")
    private TrackingEventType eventType;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    private String locationJson;

    private String detailsJson;
}

