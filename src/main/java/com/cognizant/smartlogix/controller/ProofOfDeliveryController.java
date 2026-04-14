package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CaptureProofOfDeliveryRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.ProofOfDeliveryResponse;
import com.cognizant.smartlogix.model.enums.PODStatus;
import com.cognizant.smartlogix.service.ProofOfDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pods")
@Tag(name = "Proof of Delivery", description = "POD capture and verification")
public class ProofOfDeliveryController {

    private final ProofOfDeliveryService podService;

    public ProofOfDeliveryController(ProofOfDeliveryService podService) {
        this.podService = podService;
    }

    @Operation(summary = "Capture proof of delivery")
    @PostMapping
    public ResponseEntity<ApiResponse<ProofOfDeliveryResponse>> capture(
            @Valid @RequestBody CaptureProofOfDeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("POD captured", podService.capture(request)));
    }

    @Operation(summary = "Get POD by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProofOfDeliveryResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(podService.findById(id)));
    }

    @Operation(summary = "Get POD by fulfillment ID")
    @GetMapping("/fulfillment/{fulfillmentId}")
    public ResponseEntity<ApiResponse<ProofOfDeliveryResponse>> getByFulfillment(
            @PathVariable UUID fulfillmentId) {
        return ResponseEntity.ok(ApiResponse.ok(podService.findByFulfillment(fulfillmentId)));
    }

    @Operation(summary = "Update POD status (VERIFIED / DISPUTED)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProofOfDeliveryResponse>> updateStatus(
            @PathVariable UUID id, @RequestParam PODStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("POD status updated", podService.updateStatus(id, status)));
    }
}

