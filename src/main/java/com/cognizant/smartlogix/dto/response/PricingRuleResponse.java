package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.PricingRuleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PricingRuleResponse {
    private UUID id;
    private String name;
    private String conditionsJson;
    private String calculationJson;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Integer priority;
    private PricingRuleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

