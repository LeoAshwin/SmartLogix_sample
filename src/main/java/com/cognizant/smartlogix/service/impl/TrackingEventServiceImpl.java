package com.cognizant.smartlogix.service.impl;


import com.cognizant.smartlogix.dto.Driver.request.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.request.TrackingEventRequest;
import com.cognizant.smartlogix.dto.Driver.request.TrackingMetadata;
import com.cognizant.smartlogix.exception.driver.InvalidStateTransitionException;
import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;
import io.micrometer.observation.annotation.Observed;
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
    public TrackingEvent recordEvent(String fulfillmentId, EventType newType,
                                     LocationDetails location, TrackingMetadata metadata) {

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
    public List<TrackingEvent>   getHistoryByFulfillment(String fulfillmentId) {
        List<TrackingEvent> history = trackingEventRepository.findByFulfillmentIdOrderByEventTimestampDesc(fulfillmentId);
        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No tracking history found for Fulfillment ID: " + fulfillmentId);
        }
        return history;
    }

    @Override
    public TrackingEvent getLatestStatus(String fulfillmentId) {
        return trackingEventRepository.findFirstByFulfillmentIdOrderByEventTimestampDesc(fulfillmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No tracking history found for Fulfillment ID: " + fulfillmentId));
    }


    @Override
    @Transactional
    @Observed(name = "event.sync") //Micrometer Observation(creates metrics and traces)
    public List<TrackingEvent> syncBatch(List<TrackingEventRequest> requests) {
        return requests.stream()
                .map(req -> {
                    return trackingEventRepository.findByFulfillmentIdAndEventTimestamp(
                                    req.fulfillmentId(), req.timestamp())
                            .orElseGet(() -> recordEvent(
                                    req.fulfillmentId(),
                                    req.type(),
                                    req.location(),
                                    req.metadata()));
                })
                .collect(java.util.stream.Collectors.toList());
    }
}