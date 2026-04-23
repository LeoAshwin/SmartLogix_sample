package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.pricing.CarrierSettlementCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.service.CarrierSettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriers")
public class CarrierSettlementController {

    private final CarrierSettlementService carrierSettlementService;

    public CarrierSettlementController(
            CarrierSettlementService carrierSettlementService) {
        this.carrierSettlementService = carrierSettlementService;
    }

    /**
     * Carrier submits an invoice / settlement record.
     * Finance officer or admin processes it; carrier initiates.
     */
    @PostMapping("/settlements")
    @PreAuthorize("hasAnyRole('CARRIER','FINANCE_OFFICER','ADMIN')")
    public ResponseEntity<CarrierSettlementResponse> createSettlement(
            @RequestBody CarrierSettlementCreateRequest request) {

        return ResponseEntity.ok(
                carrierSettlementService.createSettlement(request));
    }

    /**
     * View all settlements for a carrier.
     * Finance officer, logistics manager, carrier itself, and admin.
     */
    @GetMapping("/{carrierId}/settlements")
    @PreAuthorize("hasAnyRole('CARRIER','FINANCE_OFFICER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<CarrierSettlementResponse>> getCarrierSettlements(
            @PathVariable String carrierId) {

        return ResponseEntity.ok(
                carrierSettlementService.getCarrierSettlements(carrierId));
    }
}