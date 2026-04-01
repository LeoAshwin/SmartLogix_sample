package com.cognizant.smartlogix.service;
import com.cognizant.smartlogix.dto.Driver.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.TrackingMetadata;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;

import java.util.List;

public interface TrackingEventService {

    /**
     * Records a new status transition for a fulfillment.
     * Enforces deterministic state machine rules.
     */
    TrackingEvent recordEvent(Long fulfillmentId, EventType newType,
                              LocationDetails location, TrackingMetadata metadata);

    /**
     * Retrieves the full history of a fulfillment for the audit trail.
     */
    List<TrackingEvent> getHistoryByFulfillment(Long fulfillmentId);

    /**
     * Gets the current (most recent) status of a fulfillment.
     */
    TrackingEvent getLatestStatus(Long fulfillmentId);
}
