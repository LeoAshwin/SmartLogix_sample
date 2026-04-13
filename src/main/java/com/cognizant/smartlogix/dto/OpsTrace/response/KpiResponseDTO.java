package com.cognizant.smartlogix.dto.OpsTrace.response;

public record KpiResponseDTO(
        Long kpiId,
        String name,
        String value,
        String unit,
        String recordedAt
) {}