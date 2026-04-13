

package com.cognizant.smartlogix.dto.OpsTrace.request;

public record KpiRequestDTO(
        String name,
        String value,
        String unit,
        String recordedAt
) {}