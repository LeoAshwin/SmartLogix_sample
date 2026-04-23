package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;
import com.cognizant.smartlogix.service.ManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manifests")
@CrossOrigin(origins = "*")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    /** Dispatcher or Logistics Manager generates a route manifest. */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER')")
    public ResponseEntity<ManifestResponseDTO> generateManifest(@RequestBody ManifestRequestDTO request) {
        return ResponseEntity.ok(manifestService.generateDeterministicManifest(request));
    }

    /** Dispatcher or Logistics Manager overrides stop ordering. */
    @PutMapping("/{manifestId}/override")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER')")
    public ResponseEntity<ManifestResponseDTO> manualOverride(
            @PathVariable Long manifestId,
            @RequestBody List<StopDTO> stops) {
        return ResponseEntity.ok(manifestService.updateManifestManualOverride(manifestId, stops));
    }

    /** Dispatcher or Logistics Manager dispatches a manifest to the driver. */
    @PostMapping("/{manifestId}/dispatch")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER')")
    public ResponseEntity<ManifestResponseDTO> dispatchManifest(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.dispatchManifest(manifestId));
    }

    /** Read access: dispatchers, managers, driver (own manifest), admin. */
    @GetMapping("/{manifestId}")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER','DRIVER','ADMIN')")
    public ResponseEntity<ManifestResponseDTO> getManifest(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.getManifestById(manifestId));
    }

    /** Cancellation is a high-privilege operation. */
    @PostMapping("/{manifestId}/cancel")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<Void> cancelManifest(@PathVariable Long manifestId) {
        manifestService.cancelManifest(manifestId);
        return ResponseEntity.noContent().build();
    }

    /** Driver starts the trip via their mobile app. */
    @PostMapping("/{manifestId}/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ManifestResponseDTO> startTrip(@PathVariable Long manifestId) {
        return ResponseEntity.ok(manifestService.startTrip(manifestId));
    }

    /** Driver marks an individual stop as completed. */
    @PostMapping("/{manifestId}/stops/{fulfillmentId}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ManifestResponseDTO> completeStop(
            @PathVariable Long manifestId,
            @PathVariable String fulfillmentId) {
        return ResponseEntity.ok(manifestService.markStopAsCompleted(manifestId, fulfillmentId));
    }

    /** Ops search across manifests. */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<ManifestResponseDTO>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(manifestService.searchManifests(status, driverId, date));
    }

    /** Export PDF trip sheet. */
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<byte[]> downloadTripSheet(@PathVariable Long id) {
        byte[] pdfBytes = manifestService.exportManifestToPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trip_sheet_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /** Hard-delete: admin only. */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteManifest(@PathVariable Long id) {
        manifestService.deleteManifest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<ManifestResponseDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(manifestService.getManifestsByStatus(status));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<ManifestResponseDTO>> getByVehicle(@PathVariable String vehicleId) {
        return ResponseEntity.ok(manifestService.getManifestsByVehicle(vehicleId));
    }
}