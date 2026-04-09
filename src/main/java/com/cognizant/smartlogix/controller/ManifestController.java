package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.RouteLegDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.service.ManifestService;
import com.cognizant.smartlogix.service.RouteLegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing the Manifest Lifecycle.
 * Handles the workflow transitions from initial planning through execution and PDF reporting.
 */
@RestController
@RequestMapping("/api/v1/manifests")
@CrossOrigin(origins = "*")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    @Autowired
    private RouteLegService routeLegService;

    /**
     * Initializes a new logistics manifest.
     * Transitions state from GENERATED to OPTIMIZED based on deterministic routing.
     */
    @PostMapping("/generate")
    public ResponseEntity<ManifestResponseDTO> generateManifest(@RequestBody ManifestRequestDTO request) {
        return ResponseEntity.ok(manifestService.generateDeterministicManifest(request));
    }

    /**
     * Updates stop sequences via manual dispatcher override.
     * Note: This operation is restricted if the manifest status is 'STARTED'.
     */
    @PutMapping("/{manifestId}/override")
    public ResponseEntity<ManifestResponseDTO> manualOverride(
            @PathVariable Long manifestId,
            @RequestBody List<StopDTO> stops) {
        return ResponseEntity.ok(manifestService.updateManifestManualOverride(manifestId, stops));
    }

    /**
     * Finalizes the planning stage and transitions the manifest to 'DISPATCHED'.
     */
    @PostMapping("/{manifestId}/dispatch")
    public ResponseEntity<ManifestResponseDTO> dispatchManifest(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.dispatchManifest(manifestId));
    }

    /**
     * Retrieves full details of a specific manifest by its unique ID.
     */
    @GetMapping("/{manifestId}")
    public ResponseEntity<ManifestResponseDTO> getManifest(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.getManifestById(manifestId));
    }

    /**
     * Cancels an existing manifest and releases associated resources.
     */
    @PostMapping("/{manifestId}/cancel")
    public ResponseEntity<Void> cancelManifest(@PathVariable Long manifestId) {
        manifestService.cancelManifest(manifestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Records the commencement of the physical trip for the given manifest.
     */
    @PostMapping("/{manifestId}/start")
    public ResponseEntity<ManifestResponseDTO> startTrip(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.startTrip(manifestId));
    }

    /**
     * Marks a specific fulfillment stop as completed within the active trip.
     */
    @PostMapping("/{manifestId}/stops/{fulfillmentId}/complete")
    public ResponseEntity<ManifestResponseDTO> completeStop(
            @PathVariable Long manifestId,
            @PathVariable Long fulfillmentId) {
        return ResponseEntity.ok(manifestService.markStopAsCompleted(manifestId, fulfillmentId));
    }

    /**
     * Provides dynamic search functionality based on status, driver, or scheduled date.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ManifestResponseDTO>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(manifestService.searchManifests(status, driverId, date));
    }

    /**
     * Generates and downloads a binary PDF trip sheet for driver documentation.
     */
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> downloadTripSheet(@PathVariable Long id) {
        byte[] pdfBytes = manifestService.exportManifestToPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trip_sheet_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Uses deleteManifest
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManifest(@PathVariable Long id) {
        manifestService.deleteManifest(id);
        return ResponseEntity.noContent().build();
    }

    // Uses getManifestsByStatus
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ManifestResponseDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(manifestService.getManifestsByStatus(status));
    }

    // Uses getManifestsByVehicle
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ManifestResponseDTO>> getByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(manifestService.getManifestsByVehicle(vehicleId));
    }

}