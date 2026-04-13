package com.cognizant.smartlogix.dto.Driver.response;

import com.cognizant.smartlogix.dto.Driver.request.LocationDetails;
import lombok.Builder;

@Builder
public record TrackingEventResponse(
        Long eventId,
        Long fulfillmentId,
        String eventType,
        String timestamp,
        LocationDetails location
) {
    // No @Data needed, and fields are no longer 'private'
}