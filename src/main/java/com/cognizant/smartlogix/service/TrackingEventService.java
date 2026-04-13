package com.cognizant.smartlogix.service;
import com.cognizant.smartlogix.dto.Driver.request.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.request.TrackingEventRequest;
import com.cognizant.smartlogix.dto.Driver.request.TrackingMetadata;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;

import java.util.List;

public interface TrackingEventService {


    TrackingEvent recordEvent(Long fulfillmentId, EventType newType,
                              LocationDetails location, TrackingMetadata metadata);

    List<TrackingEvent> getHistoryByFulfillment(Long fulfillmentId);

    List<TrackingEvent> syncBatch(List<TrackingEventRequest> requests);



    TrackingEvent getLatestStatus(Long fulfillmentId);
}
