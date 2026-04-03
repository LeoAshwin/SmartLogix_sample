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
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId) {


        TrackingMetadata metadata = new TrackingMetadata();
        metadata.setDeviceId(deviceId != null ? deviceId : "UNKNOWN-DEVICE");
        metadata.setIsOfflineSync(false);


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