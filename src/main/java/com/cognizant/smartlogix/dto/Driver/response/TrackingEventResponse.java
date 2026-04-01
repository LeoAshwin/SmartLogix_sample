package com.cognizant.smartlogix.dto.Driver.response;

import com.cognizant.smartlogix.dto.Driver.LocationDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackingEventResponse {
    private Long eventId;
    private Long fulfillmentId;
    private String eventType;
    private String timestamp; // Formatted as String for easier Mobile consumption
    private LocationDetails location;
}