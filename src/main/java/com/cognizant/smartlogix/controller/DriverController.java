package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateDriverRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.DriverResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.enums.DriverStatus;
import com.cognizant.smartlogix.service.DriverService;
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
@RequestMapping("/drivers")
@Tag(name = "Drivers", description = "Driver profile and availability management")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @Operation(summary = "Register driver")
    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponse>> create(@Valid @RequestBody CreateDriverRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Driver registered", driverService.create(request)));
    }

    @Operation(summary = "Get driver by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(driverService.findById(id)));
    }

    @Operation(summary = "List all drivers (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<DriverResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(driverService.findAll(PageRequest.of(page, size)))));
    }

    @Operation(summary = "List available drivers")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DriverResponse>>> listAvailable() {
        return ResponseEntity.ok(ApiResponse.ok(driverService.findAvailable()));
    }

    @Operation(summary = "Update driver status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DriverResponse>> updateStatus(
            @PathVariable UUID id, @RequestParam DriverStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", driverService.updateStatus(id, status)));
    }

    @Operation(summary = "Remove driver")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        driverService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Driver removed", null));
    }
}

