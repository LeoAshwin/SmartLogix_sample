package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.GenerateSettlementRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.enums.CarrierSettlementStatus;
import com.cognizant.smartlogix.service.CarrierSettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/settlements")
@Tag(name = "Carrier Settlements", description = "Deterministic carrier fee settlement and reconciliation")
public class CarrierSettlementController {

    private final CarrierSettlementService settlementService;

    public CarrierSettlementController(CarrierSettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @Operation(summary = "Generate carrier settlement for a billing period")
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<CarrierSettlementResponse>> generate(
            @Valid @RequestBody GenerateSettlementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Settlement generated", settlementService.generate(request)));
    }

    @Operation(summary = "Get settlement by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarrierSettlementResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(settlementService.findById(id)));
    }

    @Operation(summary = "List settlements by carrier")
    @GetMapping("/carrier/{carrierId}")
    public ResponseEntity<ApiResponse<PagedResponse<CarrierSettlementResponse>>> getByCarrier(
            @PathVariable UUID carrierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(settlementService.findByCarrier(carrierId,
                        PageRequest.of(page, size, Sort.by("periodStart").descending())))));
    }

    @Operation(summary = "List settlements by status")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CarrierSettlementResponse>>> listByStatus(
            @RequestParam CarrierSettlementStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(settlementService.findByStatus(status,
                        PageRequest.of(page, size, Sort.by("generatedAt").descending())))));
    }

    @Operation(summary = "Approve settlement")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<CarrierSettlementResponse>> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Settlement approved", settlementService.approve(id)));
    }

    @Operation(summary = "Dispute settlement with discrepancy details")
    @PostMapping("/{id}/dispute")
    public ResponseEntity<ApiResponse<CarrierSettlementResponse>> dispute(
            @PathVariable UUID id, @RequestBody String discrepancyJson) {
        return ResponseEntity.ok(ApiResponse.ok("Dispute recorded", settlementService.dispute(id, discrepancyJson)));
    }

    @Operation(summary = "Mark settlement as paid")
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<CarrierSettlementResponse>> markPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Settlement marked as paid", settlementService.markPaid(id)));
    }
}

