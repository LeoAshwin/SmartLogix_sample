package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.CreateCarrierBookingRequest;
import com.cognizant.smartlogix.dto.response.CarrierBookingResponse;
import com.cognizant.smartlogix.model.enums.CarrierBookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CarrierBookingService {
    CarrierBookingResponse create(CreateCarrierBookingRequest request);
    CarrierBookingResponse findById(UUID id);
    CarrierBookingResponse findByFulfillment(UUID fulfillmentId);
    Page<CarrierBookingResponse> findByCarrier(UUID carrierId, Pageable pageable);
    CarrierBookingResponse updateStatus(UUID id, CarrierBookingStatus status);
    CarrierBookingResponse cancel(UUID id);
}

