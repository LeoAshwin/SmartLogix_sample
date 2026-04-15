package com.cognizant.smartlogix.dto.OpsTrace.request;

import java.math.BigDecimal;

/**
 * Immutable request DTO for KPI.
 */
public record KpiRequestDTO(
        String name,
        String definition,
        BigDecimal target,
        String reportingPeriod
) {
}