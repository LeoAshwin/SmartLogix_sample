package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.GenerateManifestRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.ManifestResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.service.ManifestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manifests")
@Tag(name = "Manifests", description = "Deterministic manifest generation and dispatcher operations")
public class ManifestController {

    private final ManifestService manifestService;

    public ManifestController(ManifestService manifestService) {
        this.manifestService = manifestService;
    }

    @Operation(summary = "Generate manifest deterministically")
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<ManifestResponse>> generate(
            @Valid @RequestBody GenerateManifestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Manifest generated", manifestService.generate(request)));
    }

    @Operation(summary = "Get manifest by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ManifestResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(manifestService.findById(id)));
    }

    @Operation(summary = "List all manifests (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ManifestResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(manifestService.findAll(PageRequest.of(page, size, Sort.by("date").descending())))));
    }

    @Operation(summary = "Get manifests by depot and date")
    @GetMapping("/depot/{depotId}")
    public ResponseEntity<ApiResponse<List<ManifestResponse>>> getByDepotAndDate(
            @PathVariable UUID depotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok(manifestService.findByDepotAndDate(depotId, date)));
    }

    @Operation(summary = "Publish manifest (DRAFT â†’ PUBLISHED)")
    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<ManifestResponse>> publish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Manifest published", manifestService.publish(id)));
    }

    @Operation(summary = "Mark manifest as completed")
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ManifestResponse>> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Manifest completed", manifestService.complete(id)));
    }

    @Operation(summary = "Cancel manifest")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ManifestResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Manifest cancelled", manifestService.cancel(id)));
    }

    @Operation(summary = "Dispatcher override â€” reassign driver")
    @PatchMapping("/{id}/driver")
    public ResponseEntity<ApiResponse<ManifestResponse>> reassignDriver(
            @PathVariable UUID id, @RequestParam UUID driverId) {
        return ResponseEntity.ok(ApiResponse.ok("Driver reassigned", manifestService.reassignDriver(id, driverId)));
    }

    @Operation(summary = "Dispatcher override â€” reassign vehicle")
    @PatchMapping("/{id}/vehicle")
    public ResponseEntity<ApiResponse<ManifestResponse>> reassignVehicle(
            @PathVariable UUID id, @RequestParam UUID vehicleId) {
        return ResponseEntity.ok(ApiResponse.ok("Vehicle reassigned", manifestService.reassignVehicle(id, vehicleId)));
    }
}

