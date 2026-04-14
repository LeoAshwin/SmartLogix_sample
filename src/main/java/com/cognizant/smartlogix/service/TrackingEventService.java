package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.RecordTrackingEventRequest;
import com.cognizant.smartlogix.dto.response.TrackingEventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TrackingEventService {
    TrackingEventResponse record(RecordTrackingEventRequest request);
    List<TrackingEventResponse> findByFulfillment(UUID fulfillmentId);
    Page<TrackingEventResponse> findByFulfillmentPaged(UUID fulfillmentId, Pageable pageable);
    TrackingEventResponse findById(UUID id);
}

