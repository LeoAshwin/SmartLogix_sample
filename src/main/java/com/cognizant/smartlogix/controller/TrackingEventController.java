package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Driver.request.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.request.TrackingEventRequest;
import com.cognizant.smartlogix.dto.Driver.request.TrackingMetadata;
import com.cognizant.smartlogix.dto.Driver.response.TrackingEventResponse;
import com.cognizant.smartlogix.dto.ResponseMapper;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;
import com.cognizant.smartlogix.service.TrackingEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/driver/events")
@RequiredArgsConstructor
@Slf4j
@Validated //(class and method level validation)
public class TrackingEventController {

    private final TrackingEventService trackingEventService;
    private final ResponseMapper mapper;

    /**
     * Driver records a real-time tracking event (e.g. OUT_FOR_DELIVERY).
     * Only the DRIVER role may post new events.
     */
    @PostMapping("/{fulfillmentId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TrackingEventResponse> recordEvent(
            @PathVariable String fulfillmentId,
            @RequestParam EventType type,
            @RequestBody(required = false) LocationDetails location,
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId,
            @RequestHeader(value = "X-App-Version", required = false) String appVersion) {

        TrackingMetadata metadata = new TrackingMetadata(
                deviceId != null ? deviceId : "UNKNOWN-DEVICE",
                "100%",
                false,
                String.valueOf(System.currentTimeMillis()),
                appVersion != null ? appVersion : "1.0.0"
        );

        TrackingEvent event = trackingEventService.recordEvent(
                fulfillmentId,
                type,
                location,
                metadata
        );

        return ResponseEntity.ok(mapper.toTrackingResponse(event));
    }

    /**
     * Customer, merchant and ops staff can view tracking history.
     */
    @GetMapping("/{fulfillmentId}/history")
    @PreAuthorize("hasAnyRole('CUSTOMER','MERCHANT','DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<TrackingEventResponse>> getHistory(@PathVariable String fulfillmentId) {
        List<TrackingEvent> history = trackingEventService.getHistoryByFulfillment(fulfillmentId);
        return ResponseEntity.ok(mapper.toTrackingResponseList(history));
    }

    /**
     * Driver syncs offline-captured events during network reconnect.
     * Only DRIVER role can push sync batches.
     */
    @PostMapping("/sync")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<TrackingEventResponse>> syncOfflineEvents(
            @RequestHeader("X-Sync-ID") String syncId,
            @Valid @RequestBody List<TrackingEventRequest> events) {

        log.info("Processing offline sync batch: {} with {} events", syncId, events.size());
        List<TrackingEvent> syncedEvents = trackingEventService.syncBatch(events);
        return ResponseEntity.ok(mapper.toTrackingResponseList(syncedEvents));
    }
}