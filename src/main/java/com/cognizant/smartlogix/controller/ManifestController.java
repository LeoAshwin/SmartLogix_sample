package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.service.ManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manifests")
@CrossOrigin(origins = "*")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    /**
     * STAGE 1: PLANNING
     * Generates a manifest with status GENERATED -> OPTIMIZED.
     */
    @PostMapping("/generate")
    public ResponseEntity<ManifestResponseDTO> generateManifest(@RequestBody ManifestRequestDTO request) {
        return ResponseEntity.ok(manifestService.generateDeterministicManifest(request));
    }

    /**
     * STAGE 2: OPTIMIZATION (MANUAL)
     * Allows dispatchers to re-order stops.
     * SUBJECT TO: Read-Only Lock (Fails if Status is STARTED).
     */
    @PutMapping("/{manifestId}/override")
    public ResponseEntity<ManifestResponseDTO> manualOverride(
            @PathVariable Long manifestId,
            @RequestBody List<StopDTO> stops) {
        return ResponseEntity.ok(manifestService.updateManifestManualOverride(manifestId, stops));
    }

    /**
     * STAGE 3: EXECUTION START
     * Transitions status from OPTIMIZED -> DISPATCHED.
     */
    @PostMapping("/{manifestId}/dispatch") // <--- ADD THIS
    public ResponseEntity<ManifestResponseDTO> dispatchManifest(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.dispatchManifest(manifestId));
    }

    /**
     * UTILITY: Fetch Manifest Details
     */
    @GetMapping("/{manifestId}")
    public ResponseEntity<ManifestResponseDTO> getManifest(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.getManifestById(manifestId));
    }

    /**
     * UTILITY: Delete Manifest
     */
    @PostMapping("/{manifestId}/cancel")
    public ResponseEntity<Void> cancelManifest(@PathVariable Long manifestId) {
        manifestService.cancelManifest(manifestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{manifestId}/start")
    public ResponseEntity<ManifestResponseDTO> startTrip(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.startTrip(manifestId));
    }

    @PostMapping("/{manifestId}/stops/{fulfillmentId}/complete")
    public ResponseEntity<ManifestResponseDTO> completeStop(
            @PathVariable Long manifestId,
            @PathVariable Long fulfillmentId) {

        // This calls the service logic we just finalized
        return ResponseEntity.ok(manifestService.markStopAsCompleted(manifestId, fulfillmentId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ManifestResponseDTO>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) LocalDate date) {

        return ResponseEntity.ok(manifestService.searchManifests(status, driverId, date));
    }
}