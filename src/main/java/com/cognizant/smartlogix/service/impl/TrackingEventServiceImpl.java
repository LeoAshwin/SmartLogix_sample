package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.TrackingEventMapper;
import com.cognizant.smartlogix.dto.request.RecordTrackingEventRequest;
import com.cognizant.smartlogix.dto.response.TrackingEventResponse;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Fulfillment;
import com.cognizant.smartlogix.model.entity.TrackingEvent;
import com.cognizant.smartlogix.repository.FulfillmentRepository;
import com.cognizant.smartlogix.repository.TrackingEventRepository;
import com.cognizant.smartlogix.service.TrackingEventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TrackingEventServiceImpl implements TrackingEventService {

    private final TrackingEventRepository trackingEventRepository;
    private final FulfillmentRepository fulfillmentRepository;
    private final TrackingEventMapper trackingEventMapper;

    public TrackingEventServiceImpl(TrackingEventRepository trackingEventRepository,
                                     FulfillmentRepository fulfillmentRepository,
                                     TrackingEventMapper trackingEventMapper) {
        this.trackingEventRepository = trackingEventRepository;
        this.fulfillmentRepository   = fulfillmentRepository;
        this.trackingEventMapper     = trackingEventMapper;
    }

    /** Append-only â€” never updates or deletes events */
    @Override
    public TrackingEventResponse record(RecordTrackingEventRequest request) {
        Fulfillment fulfillment = fulfillmentRepository.findById(request.getFulfillmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment", "id", request.getFulfillmentId()));

        TrackingEvent event = TrackingEvent.builder()
                .fulfillment(fulfillment)
                .eventType(request.getEventType())
                .timestamp(request.getTimestamp())
                .locationJson(request.getLocationJson())
                .detailsJson(request.getDetailsJson())
                .build();

        return trackingEventMapper.toResponse(trackingEventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackingEventResponse> findByFulfillment(UUID fulfillmentId) {
        return trackingEventRepository
                .findByFulfillmentIdOrderByTimestampAsc(fulfillmentId)
                .stream()
                .map(trackingEventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackingEventResponse> findByFulfillmentPaged(UUID fulfillmentId, Pageable pageable) {
        return trackingEventRepository.findByFulfillmentId(fulfillmentId, pageable)
                .map(trackingEventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackingEventResponse findById(UUID id) {
        return trackingEventMapper.toResponse(
                trackingEventRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("TrackingEvent", "id", id)));
    }
}

