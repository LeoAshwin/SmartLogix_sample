package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.CreateVehicleRequest;
import com.cognizant.smartlogix.dto.response.VehicleResponse;
import com.cognizant.smartlogix.model.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
    VehicleResponse create(CreateVehicleRequest request);
    VehicleResponse findById(UUID id);
    Page<VehicleResponse> findAll(Pageable pageable);
    List<VehicleResponse> findAvailable();
    VehicleResponse updateStatus(UUID id, VehicleStatus status);
    void delete(UUID id);
}

