package com.cognizant.smartlogix.service;
import com.cognizant.smartlogix.dto.Driver.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.TrackingMetadata;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;

import java.util.List;

public interface TrackingEventService {


    TrackingEvent recordEvent(Long fulfillmentId, EventType newType,
                              LocationDetails location, TrackingMetadata metadata);

    List<TrackingEvent> getHistoryByFulfillment(Long fulfillmentId);


    TrackingEvent getLatestStatus(Long fulfillmentId);
}
