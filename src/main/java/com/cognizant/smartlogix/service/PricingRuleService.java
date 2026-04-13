package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.pricing.PricingRuleRequest;
import com.cognizant.smartlogix.dto.pricing.response.PricingRuleResponse;

import java.util.List;

public interface PricingRuleService {

    PricingRuleResponse createPricingRule(PricingRuleRequest request);

    List<PricingRuleResponse> getAllPricingRules();
}
