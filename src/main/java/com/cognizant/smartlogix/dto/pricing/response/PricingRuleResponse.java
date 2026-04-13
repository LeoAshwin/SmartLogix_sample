package com.cognizant.smartlogix.dto.pricing.response;

import com.cognizant.smartlogix.model.data.PricingRuleStatus;
import java.time.LocalDateTime;

public record PricingRuleResponse(
        Long ruleId,
        String name,
        String conditionsJson,
        String calculationJson,
        LocalDateTime effectiveFrom,
        LocalDateTime effectiveTo,
        Integer priority,
        PricingRuleStatus status
) {
}