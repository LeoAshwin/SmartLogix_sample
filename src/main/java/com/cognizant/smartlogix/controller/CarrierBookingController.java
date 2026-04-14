package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateCarrierBookingRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.CarrierBookingResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.enums.CarrierBookingStatus;
import com.cognizant.smartlogix.service.CarrierBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carrier-bookings")
@Tag(name = "Carrier Bookings", description = "Carrier booking management")
public class CarrierBookingController {

    private final CarrierBookingService bookingService;

    public CarrierBookingController(CarrierBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Create carrier booking")
    @PostMapping
    public ResponseEntity<ApiResponse<CarrierBookingResponse>> create(
            @Valid @RequestBody CreateCarrierBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Booking created", bookingService.create(request)));
    }

    @Operation(summary = "Get booking by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarrierBookingResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.findById(id)));
    }

    @Operation(summary = "Get booking by fulfillment ID")
    @GetMapping("/fulfillment/{fulfillmentId}")
    public ResponseEntity<ApiResponse<CarrierBookingResponse>> getByFulfillment(
            @PathVariable UUID fulfillmentId) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.findByFulfillment(fulfillmentId)));
    }

    @Operation(summary = "List bookings by carrier")
    @GetMapping("/carrier/{carrierId}")
    public ResponseEntity<ApiResponse<PagedResponse<CarrierBookingResponse>>> getByCarrier(
            @PathVariable UUID carrierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(bookingService.findByCarrier(carrierId, PageRequest.of(page, size)))));
    }

    @Operation(summary = "Update booking status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CarrierBookingResponse>> updateStatus(
            @PathVariable UUID id, @RequestParam CarrierBookingStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", bookingService.updateStatus(id, status)));
    }

    @Operation(summary = "Cancel booking")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<CarrierBookingResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled", bookingService.cancel(id)));
    }
}

