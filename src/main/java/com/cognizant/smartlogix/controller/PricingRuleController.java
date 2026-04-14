package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreatePricingRuleRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.dto.response.PricingRuleResponse;
import com.cognizant.smartlogix.service.PricingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pricing-rules")
@Tag(name = "Pricing Rules", description = "Deterministic pricing rule management")
public class PricingRuleController {

    private final PricingRuleService pricingRuleService;

    public PricingRuleController(PricingRuleService pricingRuleService) {
        this.pricingRuleService = pricingRuleService;
    }

    @Operation(summary = "Create pricing rule (starts INACTIVE)")
    @PostMapping
    public ResponseEntity<ApiResponse<PricingRuleResponse>> create(
            @Valid @RequestBody CreatePricingRuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Pricing rule created", pricingRuleService.create(request)));
    }

    @Operation(summary = "Get pricing rule by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PricingRuleResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(pricingRuleService.findById(id)));
    }

    @Operation(summary = "List all pricing rules (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PricingRuleResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(pricingRuleService.findAll(PageRequest.of(page, size)))));
    }

    @Operation(summary = "Get active pricing rules for a given date (for pricing engine)")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PricingRuleResponse>>> getActiveForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok(pricingRuleService.findActiveForDate(date)));
    }

    @Operation(summary = "Activate pricing rule")
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PricingRuleResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Rule activated", pricingRuleService.activate(id)));
    }

    @Operation(summary = "Deactivate pricing rule")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<PricingRuleResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Rule deactivated", pricingRuleService.deactivate(id)));
    }

    @Operation(summary = "Archive pricing rule")
    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<PricingRuleResponse>> archive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Rule archived", pricingRuleService.archive(id)));
    }
}

