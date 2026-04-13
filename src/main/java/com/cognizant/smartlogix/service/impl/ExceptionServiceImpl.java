package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;
import com.cognizant.smartlogix.model.data.DeliveryStatus;
import com.cognizant.smartlogix.repository.PodRepository;
import lombok.RequiredArgsConstructor;
import com.cognizant.smartlogix.model.DeliveryException;
import com.cognizant.smartlogix.model.data.EventType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cognizant.smartlogix.repository.DeliveryExceptionRepository;
import com.cognizant.smartlogix.service.ExceptionService;
import com.cognizant.smartlogix.service.TrackingEventService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExceptionServiceImpl implements ExceptionService {

    private final DeliveryExceptionRepository exceptionRepository;
    private final TrackingEventService trackingEventService;
    private final PodRepository podRepository;

    @Override
    @Transactional
    public DeliveryException reportException(Long fulfillmentId, Long driverId,
                                             String reasonCode, String details) {

        // 1. Calculate current state before building
        int previousAttempts = exceptionRepository.findByFulfillmentIdOrderByRaisedAtDesc(fulfillmentId).size();
        int newRetryCount = previousAttempts + 1;

        // 2. Deterministic Logic for the suggested action
        String action = (newRetryCount >= 3) ? "RETURN_TO_HUB" : "REATTEMPT_NEXT_WINDOW";

        DeliveryException ex = DeliveryException.builder()
                .fulfillmentId(fulfillmentId)
                .raisedBy(driverId)
                .reasonCode(reasonCode)
                .details(details)
                .raisedAt(LocalDateTime.now())
                .status(DeliveryStatus.OPEN)
                .retryCount(newRetryCount)
                .suggestedAction(action) // Set the calculated action here
                .build();


        // 4. Update tracking history
        trackingEventService.recordEvent(fulfillmentId, EventType.FAILED, null, null);
        return exceptionRepository.save(ex);
    }

    @Override
    public List<DeliveryException> getOpenExceptions() {
        return exceptionRepository.findByStatusOrderByRaisedAtDesc(DeliveryStatus.OPEN);
    }

    @Override
    @Transactional
    public void escalateException(Long exceptionId) {
        DeliveryException ex = exceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot escalate: Exception ID " + exceptionId + " not found."));

        ex.setStatus(DeliveryStatus.ESCALATED);
        exceptionRepository.save(ex);
    }

    @Override
    @Transactional
    public DeliveryException resolveException(Long exceptionId, String notes) {
        DeliveryException ex = exceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot resolve: Exception ID " + exceptionId + " not found."));

        ex.setStatus(DeliveryStatus.RESOLVED);
        ex.setDetails(ex.getDetails() + " | Resolution: " + notes);
        return exceptionRepository.save(ex);
    }

    // Add to ExceptionServiceImpl.java

    @Override
    public List<String> getAvailableReasonCodes() {
        // These should match your business logic rules for deterministic reattempts
        return List.of(
                "CUSTOMER_UNAVAILABLE",
                "ACCESS_CODE_REQUIRED",
                "RECIPIENT_REFUSED",
                "WEATHER_DELAY",
                "DAMAGED_IN_TRANSIT"
        );
    }

    @Override
    public List<DeliveryException> getExceptionsByStatus(String status) {
        try {
            DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());
            return exceptionRepository.findByStatusOrderByRaisedAtDesc(deliveryStatus);
        } catch (IllegalArgumentException e) {
            throw new com.cognizant.smartlogix.exception.driver.ResourceNotFoundException("Invalid status: " + status);
        }
    }


}