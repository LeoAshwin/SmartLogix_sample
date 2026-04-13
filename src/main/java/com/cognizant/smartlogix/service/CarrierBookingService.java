package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.pricing.CarrierBookingCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.CarrierBookingResponse;

import java.util.List;

public interface CarrierBookingService {


    CarrierBookingResponse createBooking(
            CarrierBookingCreateRequest request);

    List<CarrierBookingResponse> getCarrierBookings(Long carrierId);
}