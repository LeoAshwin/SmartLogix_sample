package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.RecordTrackingEventRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.dto.response.TrackingEventResponse;
import com.cognizant.smartlogix.service.TrackingEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tracking-events")
@Tag(name = "Tracking Events", description = "Append-only fulfillment tracking history")
public class TrackingEventController {

    private final TrackingEventService trackingEventService;

    public TrackingEventController(TrackingEventService trackingEventService) {
        this.trackingEventService = trackingEventService;
    }

    @Operation(summary = "Record tracking event (append-only)")
    @PostMapping
    public ResponseEntity<ApiResponse<TrackingEventResponse>> record(
            @Valid @RequestBody RecordTrackingEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Event recorded", trackingEventService.record(request)));
    }

    @Operation(summary = "Get full tracking history for a fulfillment")
    @GetMapping("/fulfillment/{fulfillmentId}")
    public ResponseEntity<ApiResponse<List<TrackingEventResponse>>> getByFulfillment(
            @PathVariable UUID fulfillmentId) {
        return ResponseEntity.ok(ApiResponse.ok(trackingEventService.findByFulfillment(fulfillmentId)));
    }

    @Operation(summary = "Get tracking event by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrackingEventResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(trackingEventService.findById(id)));
    }
}

