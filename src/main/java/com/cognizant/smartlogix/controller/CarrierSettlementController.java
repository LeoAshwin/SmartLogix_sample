package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.pricing.CarrierSettlementCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.service.CarrierSettlementService;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/settlements")
    public ResponseEntity<CarrierSettlementResponse> createSettlement(
            @RequestBody CarrierSettlementCreateRequest request) {

        return ResponseEntity.ok(
                carrierSettlementService.createSettlement(request));
    }


    @GetMapping("/{carrierId}/settlements")
    public ResponseEntity<List<CarrierSettlementResponse>> getCarrierSettlements(
            @PathVariable Long carrierId) {

        return ResponseEntity.ok(
                carrierSettlementService.getCarrierSettlements(carrierId));
    }
}