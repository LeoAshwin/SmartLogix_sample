package com.cognizant.smartlogix.dto.OpsTrace.response;

import java.math.BigDecimal;

/**
 * Immutable response DTO for KPI.
 */
public record KpiResponseDTO(
        Long kpiId,
        String name,
        String definition,
        BigDecimal target,
        BigDecimal currentValue,
        String reportingPeriod
) {
}