package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateVehicleRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.dto.response.VehicleResponse;
import com.cognizant.smartlogix.model.enums.VehicleStatus;
import com.cognizant.smartlogix.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicles", description = "Fleet vehicle management")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Register vehicle")
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> create(@Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Vehicle registered", vehicleService.create(request)));
    }

    @Operation(summary = "Get vehicle by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.findById(id)));
    }

    @Operation(summary = "List all vehicles (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<VehicleResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(vehicleService.findAll(PageRequest.of(page, size)))));
    }

    @Operation(summary = "List available vehicles")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> listAvailable() {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.findAvailable()));
    }

    @Operation(summary = "Update vehicle status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateStatus(
            @PathVariable UUID id, @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", vehicleService.updateStatus(id, status)));
    }

    @Operation(summary = "Remove vehicle")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle removed", null));
    }
}

