package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateCarrierRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.CarrierResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.enums.CarrierStatus;
import com.cognizant.smartlogix.service.CarrierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carriers")
@Tag(name = "Carriers", description = "Carrier / 3PL onboarding and management")
public class CarrierController {

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @Operation(summary = "Onboard carrier")
    @PostMapping
    public ResponseEntity<ApiResponse<CarrierResponse>> create(@Valid @RequestBody CreateCarrierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Carrier onboarded", carrierService.create(request)));
    }

    @Operation(summary = "Get carrier by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarrierResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(carrierService.findById(id)));
    }

    @Operation(summary = "List all carriers (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CarrierResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(carrierService.findAll(PageRequest.of(page, size)))));
    }

    @Operation(summary = "Update carrier status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CarrierResponse>> updateStatus(
            @PathVariable UUID id, @RequestParam CarrierStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", carrierService.updateStatus(id, status)));
    }

    @Operation(summary = "Remove carrier")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        carrierService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Carrier removed", null));
    }
}

