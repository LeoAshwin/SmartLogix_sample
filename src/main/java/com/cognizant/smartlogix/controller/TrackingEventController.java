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
@RequiredArgsConstructor //Testability:,Immutability,No Circular Dependencies compared to @Autowired
public class TrackingEventController {

    private final TrackingEventService trackingEventService;
    private final ResponseMapper mapper;


    @PostMapping("/{fulfillmentId}")
    public ResponseEntity<TrackingEventResponse> recordEvent(
            @PathVariable Long fulfillmentId,
            @RequestParam EventType type,
            @RequestBody(required = false) LocationDetails location,
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId,
            @RequestHeader(value = "X-App-Version", required = false) String appVersion) {

        // You must provide ALL fields defined in the record header at once
        TrackingMetadata metadata = new TrackingMetadata(
                deviceId != null ? deviceId : "UNKNOWN-DEVICE", // deviceId
                "100%",                                        // batteryLevel (placeholder)
                false,                                         // isOfflineSync
                String.valueOf(System.currentTimeMillis()),    // deviceLocalTimestamp
                appVersion != null ? appVersion : "1.0.0"      // appVersion
        );

        TrackingEvent event = trackingEventService.recordEvent(
                fulfillmentId,
                type,
                location,
                metadata
        );

        return ResponseEntity.ok(mapper.toTrackingResponse(event));
    }

    @GetMapping("/{fulfillmentId}/history")
    public ResponseEntity<List<TrackingEventResponse>> getHistory(@PathVariable Long fulfillmentId) {
        List<TrackingEvent> history = trackingEventService.getHistoryByFulfillment(fulfillmentId);
        return ResponseEntity.ok(mapper.toTrackingResponseList(history));
    }
}