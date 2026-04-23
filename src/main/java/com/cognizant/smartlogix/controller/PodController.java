package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Driver.response.PodResponse;
import com.cognizant.smartlogix.dto.Driver.request.PodSubmissionRequest;
import com.cognizant.smartlogix.dto.ResponseMapper;
import com.cognizant.smartlogix.model.Pod;
import com.cognizant.smartlogix.service.PodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/driver/pod")
@RequiredArgsConstructor
public class PodController {

    private final PodService podService;
    private final ResponseMapper mapper;

    /**
     * Driver submits Proof of Delivery (photo/signature/location).
     * Only DRIVER may submit POD.
     */
    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<PodResponse> submitPod(@Valid @RequestBody PodSubmissionRequest request) {
        boolean exists = podService.getPodByFulfillment(request.fulfillmentId()).isPresent();

        Pod pod = podService.submitPod(request);

        if (exists) {
            return ResponseEntity.ok(mapper.toPodResponse(pod));
        } else {
            return new ResponseEntity<>(mapper.toPodResponse(pod), HttpStatus.CREATED);
        }
    }

    /**
     * Retrieve POD for a fulfillment.
     * Driver (own delivery), dispatcher, manager, admin can view.
     */
    @GetMapping("/fulfillment/{fulfillmentId}")
    @PreAuthorize("hasAnyRole('DRIVER','DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<PodResponse> getPod(@PathVariable String fulfillmentId) {
        return podService.getPodByFulfillment(fulfillmentId)
                .map(pod -> ResponseEntity.ok(mapper.toPodResponse(pod)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Integrity check (tamper-proof hash verification).
     * Operational roles only.
     */
    @GetMapping("/{podId}/verify")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<String> verifyPod(@PathVariable Long podId) {
        boolean isValid = podService.verifyPodIntegrity(podId);
        return ResponseEntity.ok("POD Integrity Verified: " + isValid);
    }
}