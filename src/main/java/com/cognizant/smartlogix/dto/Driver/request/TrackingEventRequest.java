package com.cognizant.smartlogix.dto.Driver.request;

import com.cognizant.smartlogix.model.data.EventType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TrackingEventRequest(
        @NotNull Long fulfillmentId,
        @NotNull EventType type,
        @NotNull LocalDateTime timestamp, // The actual time the event happened offline
        LocationDetails location,
        TrackingMetadata metadata
) {}