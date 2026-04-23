package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.pricing.CarrierBookingCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.CarrierBookingResponse;
import com.cognizant.smartlogix.service.CarrierBookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriers")
public class CarrierBookingController {

    private final CarrierBookingService carrierBookingService;

    public CarrierBookingController(
            CarrierBookingService carrierBookingService) {
        this.carrierBookingService = carrierBookingService;
    }

    /**
     * Create a new carrier booking for a fulfillment.
     * Dispatcher or logistics manager books on behalf of the operation;
     * carrier and admin also permitted.
     */
    @PostMapping("/bookings")
    @PreAuthorize("hasAnyRole('CARRIER','DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<CarrierBookingResponse> createBooking(
            @RequestBody CarrierBookingCreateRequest request) {

        return ResponseEntity.ok(
                carrierBookingService.createBooking(request));
    }

    /**
     * Retrieve all bookings for a specific carrier.
     */
    @GetMapping("/{carrierId}/bookings")
    @PreAuthorize("hasAnyRole('CARRIER','DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<CarrierBookingResponse>> getCarrierBookings(
            @PathVariable String carrierId) {

        return ResponseEntity.ok(
                carrierBookingService.getCarrierBookings(carrierId));
    }
}