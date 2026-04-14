package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.CreateFulfillmentRequest;
import com.cognizant.smartlogix.dto.request.UpdateFulfillmentStatusRequest;
import com.cognizant.smartlogix.dto.response.FulfillmentResponse;
import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FulfillmentService {

    /** Idempotent ingestion â€” returns existing if orderId already exists */
    FulfillmentResponse ingest(CreateFulfillmentRequest request);

    FulfillmentResponse findById(UUID id);

    FulfillmentResponse findByOrderId(String orderId);

    Page<FulfillmentResponse> findAll(Pageable pageable);

    Page<FulfillmentResponse> findByStatus(FulfillmentStatus status, Pageable pageable);

    Page<FulfillmentResponse> findByMerchant(UUID merchantId, Pageable pageable);

    FulfillmentResponse updateStatus(UUID id, UpdateFulfillmentStatusRequest request);

    void delete(UUID id);
}

