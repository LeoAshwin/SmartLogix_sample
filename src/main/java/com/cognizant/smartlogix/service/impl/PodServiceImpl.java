package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.Driver.PodSubmissionRequest;
import com.cognizant.smartlogix.exception.driver.IntegrityCheckException;
import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;
import com.cognizant.smartlogix.model.data.PodStatus;
import lombok.RequiredArgsConstructor;
import com.cognizant.smartlogix.model.Pod;
import com.cognizant.smartlogix.model.data.EventType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cognizant.smartlogix.repository.PodRepository;
import com.cognizant.smartlogix.service.PodService;
import com.cognizant.smartlogix.service.TrackingEventService;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PodServiceImpl implements PodService {

    private final PodRepository podRepository;
    private final TrackingEventService trackingEventService;

    @Override
    @Transactional
    public Pod submitPod(PodSubmissionRequest request) {
        // 1. Idempotency check - notice the change from .getFulfillmentId() to .fulfillmentId()
        if (podRepository.existsByFulfillmentId(request.fulfillmentId())) {
            return podRepository.findByFulfillmentId(request.fulfillmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("POD record disappeared unexpectedly"));
        }

        Pod pod = Pod.builder()
                .status(PodStatus.SUBMITTED)
                .fulfillmentId(request.fulfillmentId()) // Changed
                .deliveredAt(LocalDateTime.now())
                .deliveredBy(request.driverId())        // Changed
                .photoUrisJson(request.photoUris())     // Changed
                .signatureUri(request.signatureUri())   // Changed
                .quantityDelivered(request.quantityDelivered() != null ? request.quantityDelivered() : 1) // Changed
                .notes(request.notes())                 // Changed
                .checksumSha256(calculateSHA256(request.signatureUri())) // Changed
                .build();

        // Trigger the State Machine update - Update all 4 parameters here
        trackingEventService.recordEvent(
                request.fulfillmentId(), // Changed
                EventType.DELIVERED,
                request.location(),      // Changed
                request.metadata()       // Changed
        );

        return podRepository.save(pod);
    }
    @Override
    public Optional<Pod> getPodByFulfillment(Long fulfillmentId) {
        return podRepository.findByFulfillmentId(fulfillmentId);
    }

    @Override
    public boolean verifyPodIntegrity(Long podId) {
        Pod pod = podRepository.findById(podId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot verify: Pod ID " + podId + " not found"));

        String currentHash = calculateSHA256(pod.getSignatureUri());
        if (!currentHash.equals(pod.getChecksumSha256())) {
            throw new IntegrityCheckException("Data tampering detected! Hash mismatch for Pod ID: " + podId);
        }
        return true;
    }

    private String calculateSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash calculation failed", e);
        }
    }
}