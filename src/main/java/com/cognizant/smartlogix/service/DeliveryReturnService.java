package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.InitiateReturnRequest;
import com.cognizant.smartlogix.dto.response.DeliveryReturnResponse;
import com.cognizant.smartlogix.model.enums.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DeliveryReturnService {
    DeliveryReturnResponse initiate(InitiateReturnRequest request);
    DeliveryReturnResponse findById(UUID id);
    Page<DeliveryReturnResponse> findByStatus(ReturnStatus status, Pageable pageable);
    DeliveryReturnResponse updateStatus(UUID id, ReturnStatus status);
    DeliveryReturnResponse receiveAndInspect(UUID id, String inspectionResultJson);
}

