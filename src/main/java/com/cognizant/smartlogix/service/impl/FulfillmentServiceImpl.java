package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.Manager.FulfillmentRequest;
import com.cognizant.smartlogix.dto.Manager.FulfillmentResponse;
import com.cognizant.smartlogix.exception.manager.InvalidOrderException;
import com.cognizant.smartlogix.model.Fulfillment;
import com.cognizant.smartlogix.model.data.FulfillmentStatus;
import com.cognizant.smartlogix.model.data.ServiceLevel;
import com.cognizant.smartlogix.repository.FulfillmentRepository;
import com.cognizant.smartlogix.service.FulfillmentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of FulfillmentService.
 *
 * Module 4.1 – Order Ingestion & Validation
 */
@Service
public class FulfillmentServiceImpl implements FulfillmentService {

    private final FulfillmentRepository fulfillmentRepository;

    public FulfillmentServiceImpl(FulfillmentRepository fulfillmentRepository) {
        this.fulfillmentRepository = fulfillmentRepository;
    }

    /**
     * Creates a new fulfillment order after validation.
     */
    @Override
    public FulfillmentResponse createFulfillment(FulfillmentRequest request) {

        // ===============================
        // Business Validations
        // ===============================
        if (request.packageWeightKg() == null || request.packageWeightKg() <= 0) {
            throw new InvalidOrderException("Package weight must be greater than zero");
        }

        if (request.serviceLevel() == null || request.serviceLevel().isBlank()) {
            throw new InvalidOrderException("Service level is mandatory");
        }

        // ===============================
        // Entity Creation
        // ===============================
        Fulfillment fulfillment = new Fulfillment();

        fulfillment.setFulfillmentId(UUID.randomUUID().toString());
        fulfillment.setOrderId(request.orderId());
        fulfillment.setMerchantId(request.merchantId());
        fulfillment.setServiceZoneId(request.serviceZoneId());
        fulfillment.setServiceLevel(ServiceLevel.valueOf(request.serviceLevel()));

        // ✅ Double → BigDecimal conversion (CRITICAL FIX)
        fulfillment.setPackageWeightKg(
                BigDecimal.valueOf(request.packageWeightKg())
        );

        fulfillment.setPackageVolumeM3(
                request.packageVolumeM3() == null
                        ? null
                        : BigDecimal.valueOf(request.packageVolumeM3())
        );

        fulfillment.setDimensionsJson(request.dimensionsJson());
        fulfillment.setDeliveryWindowStart(request.deliveryWindowStart());
        fulfillment.setDeliveryWindowEnd(request.deliveryWindowEnd());
        fulfillment.setStatus(FulfillmentStatus.PENDING);
        fulfillment.setCreatedAt(LocalDateTime.now());
        fulfillment.setUpdatedAt(LocalDateTime.now());

        // ===============================
        // Persist
        // ===============================
        fulfillmentRepository.save(fulfillment);

        // ===============================
        // Response
        // ===============================
        return new FulfillmentResponse(
                fulfillment.getFulfillmentId(),
                fulfillment.getStatus().name(),
                fulfillment.getCreatedAt()
        );
    }

    /**
     * Fetch fulfillment details by ID.
     */
    @Override
    public FulfillmentResponse getFulfillmentById(String fulfillmentId) {

        Fulfillment fulfillment = fulfillmentRepository.findById(fulfillmentId)
                .orElseThrow(() ->
                        new InvalidOrderException("Fulfillment not found with ID: " + fulfillmentId)
                );

        return new FulfillmentResponse(
                fulfillment.getFulfillmentId(),
                fulfillment.getStatus().name(),
                fulfillment.getCreatedAt()
        );
    }
}