package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Driver.LocationDetails;
import com.cognizant.smartlogix.dto.Driver.TrackingMetadata;
import com.cognizant.smartlogix.dto.Driver.response.TrackingEventResponse;
import com.cognizant.smartlogix.dto.ResponseMapper;
import com.cognizant.smartlogix.model.TrackingEvent;
import com.cognizant.smartlogix.model.data.EventType;
import com.cognizant.smartlogix.service.TrackingEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/driver/events")
@RequiredArgsConstructor
public class TrackingEventController {

    private final TrackingEventService trackingEventService;
    private final ResponseMapper mapper; // Inject Mapper

    // Requirement 4.5: Record a new status transition
    @PostMapping("/{fulfillmentId}")
    public ResponseEntity<TrackingEventResponse> recordEvent(
            @PathVariable Long fulfillmentId,
            @RequestParam EventType type,
            @RequestBody(required = false) LocationDetails location,
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId) {

        // 1. Prepare Metadata (matching your DTO requirements)
        TrackingMetadata metadata = new TrackingMetadata();
        metadata.setDeviceId(deviceId != null ? deviceId : "UNKNOWN-DEVICE");
        metadata.setIsOfflineSync(false); // Default for live recording

        // 2. Call Service with all 4 required parameters
        TrackingEvent event = trackingEventService.recordEvent(
                fulfillmentId,
                type,
                location,
                metadata
        );

        // 3. Map to Response DTO and return
        return ResponseEntity.ok(mapper.toTrackingResponse(event));
    }

    @GetMapping("/{fulfillmentId}/history")
    public ResponseEntity<List<TrackingEventResponse>> getHistory(@PathVariable Long fulfillmentId) {
        List<TrackingEvent> history = trackingEventService.getHistoryByFulfillment(fulfillmentId);
        return ResponseEntity.ok(mapper.toTrackingResponseList(history));
    }
}