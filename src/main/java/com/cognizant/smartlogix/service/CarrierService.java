package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.CreateCarrierRequest;
import com.cognizant.smartlogix.dto.response.CarrierResponse;
import com.cognizant.smartlogix.model.enums.CarrierStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CarrierService {
    CarrierResponse create(CreateCarrierRequest request);
    CarrierResponse findById(UUID id);
    Page<CarrierResponse> findAll(Pageable pageable);
    CarrierResponse updateStatus(UUID id, CarrierStatus status);
    void delete(UUID id);
}

