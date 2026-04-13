package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.pricing.PricingRuleRequest;
import com.cognizant.smartlogix.dto.pricing.response.PricingRuleResponse;
import com.cognizant.smartlogix.service.PricingRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing-rules")
public class PricingRuleController {

    private final PricingRuleService pricingRuleService;

    public PricingRuleController(PricingRuleService pricingRuleService) {
        this.pricingRuleService = pricingRuleService;
    }

    @PostMapping
    public ResponseEntity<PricingRuleResponse> createRule(
            @RequestBody PricingRuleRequest request) {
        return ResponseEntity.ok(
                pricingRuleService.createPricingRule(request));
    }

    @GetMapping
    public ResponseEntity<List<PricingRuleResponse>> getAllRules() {
        return ResponseEntity.ok(
                pricingRuleService.getAllPricingRules());
    }
}
