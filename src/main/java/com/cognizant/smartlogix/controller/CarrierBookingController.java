package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.pricing.CarrierBookingCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.CarrierBookingResponse;
import com.cognizant.smartlogix.service.CarrierBookingService;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/bookings")
    public ResponseEntity<CarrierBookingResponse> createBooking(
            @RequestBody CarrierBookingCreateRequest request) {

        return ResponseEntity.ok(
                carrierBookingService.createBooking(request));
    }


    @GetMapping("/{carrierId}/bookings")
    public ResponseEntity<List<CarrierBookingResponse>> getCarrierBookings(
            @PathVariable String carrierId) {

        return ResponseEntity.ok(
                carrierBookingService.getCarrierBookings(carrierId));
    }
}