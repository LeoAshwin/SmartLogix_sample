package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.pricing.PricingRuleRequest;
import com.cognizant.smartlogix.dto.pricing.response.PricingRuleResponse;
import com.cognizant.smartlogix.service.PricingRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing-rules")
public class PricingRuleController {

    private final PricingRuleService pricingRuleService;

    public PricingRuleController(PricingRuleService pricingRuleService) {
        this.pricingRuleService = pricingRuleService;
    }

    /**
     * Creates a new pricing rule.
     * Finance Officer defines pricing; ADMIN configures system-wide rules.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE_OFFICER')")
    public ResponseEntity<PricingRuleResponse> createRule(
            @RequestBody PricingRuleRequest request) {
        return ResponseEntity.ok(
                pricingRuleService.createPricingRule(request));
    }

    /**
     * List all pricing rules.
     * Logistics Manager also needs visibility for SLA-cost trade-offs.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE_OFFICER','LOGISTICS_MANAGER')")
    public ResponseEntity<List<PricingRuleResponse>> getAllRules() {
        return ResponseEntity.ok(
                pricingRuleService.getAllPricingRules());
    }
}
