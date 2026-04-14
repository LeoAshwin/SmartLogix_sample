package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.RaiseDeliveryExceptionRequest;
import com.cognizant.smartlogix.dto.response.DeliveryExceptionResponse;
import com.cognizant.smartlogix.model.enums.ExceptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DeliveryExceptionService {
    DeliveryExceptionResponse raise(RaiseDeliveryExceptionRequest request);
    DeliveryExceptionResponse findById(UUID id);
    List<DeliveryExceptionResponse> findByFulfillment(UUID fulfillmentId);
    Page<DeliveryExceptionResponse> findByStatus(ExceptionStatus status, Pageable pageable);
    DeliveryExceptionResponse resolve(UUID id, String resolution);
    DeliveryExceptionResponse escalate(UUID id);
    long countOpen();
}

