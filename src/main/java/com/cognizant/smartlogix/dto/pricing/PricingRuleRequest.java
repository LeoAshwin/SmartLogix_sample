package com.cognizant.smartlogix.dto.pricing;

import java.time.LocalDateTime;

public record PricingRuleRequest(
        String name,
        String conditionsJson,
        String calculationJson,
        LocalDateTime effectiveFrom,
        LocalDateTime effectiveTo,
        Integer priority
) {
}