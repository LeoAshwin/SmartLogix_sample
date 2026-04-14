package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.CreateDriverRequest;
import com.cognizant.smartlogix.dto.response.DriverResponse;
import com.cognizant.smartlogix.model.enums.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DriverService {
    DriverResponse create(CreateDriverRequest request);
    DriverResponse findById(UUID id);
    Page<DriverResponse> findAll(Pageable pageable);
    List<DriverResponse> findAvailable();
    DriverResponse updateStatus(UUID id, DriverStatus status);
    void delete(UUID id);
}

