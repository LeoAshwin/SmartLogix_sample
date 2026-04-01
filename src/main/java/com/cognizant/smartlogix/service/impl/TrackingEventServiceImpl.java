package com.cognizant.smartlogix.service.impl;


import com.cognizant.smartlogix.dto.Driver.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.TrackingMetadata;
import com.cognizant.smartlogix.exception.driver.InvalidStateTransitionException;
import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cognizant.smartlogix.repository.TrackingEventRepository;
import com.cognizant.smartlogix.service.TrackingEventService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class TrackingEventServiceImpl implements TrackingEventService {

    private final TrackingEventRepository trackingEventRepository;

    @Override
    @Transactional
    public TrackingEvent recordEvent(Long fulfillmentId, EventType newType,
                                     LocationDetails location, TrackingMetadata metadata) {

        // Requirement 4.5: Validate State Transition using Custom Exception
        trackingEventRepository.findFirstByFulfillmentIdOrderByEventTimestampDesc(fulfillmentId)
                .ifPresent(lastEvent -> {
                    if (lastEvent.getEventType() == EventType.DELIVERED) {
                        throw new InvalidStateTransitionException("Cannot update status: Fulfillment is already DELIVERED.");
                    }
                });

        TrackingEvent event = TrackingEvent.builder()
                .fulfillmentId(fulfillmentId)
                .eventType(newType)
                .locationJson(location)
                .detailsJson(metadata)
                .eventTimestamp(LocalDateTime.now())
                .build();

        return trackingEventRepository.save(event);
    }

    @Override
    public List<TrackingEvent> getHistoryByFulfillment(Long fulfillmentId) {
        List<TrackingEvent> history = trackingEventRepository.findByFulfillmentIdOrderByEventTimestampDesc(fulfillmentId);

        // If the list is empty, trigger the custom exception
        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No tracking history found for Fulfillment ID: " + fulfillmentId);
        }

        return history;
    }

    @Override
    public TrackingEvent getLatestStatus(Long fulfillmentId) {
        return trackingEventRepository.findFirstByFulfillmentIdOrderByEventTimestampDesc(fulfillmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No tracking history found for Fulfillment ID: " + fulfillmentId));
    }
}