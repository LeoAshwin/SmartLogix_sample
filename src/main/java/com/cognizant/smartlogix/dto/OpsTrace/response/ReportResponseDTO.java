package com.cognizant.smartlogix.dto.OpsTrace.response;

import java.time.LocalDateTime;

/**
 * Immutable response DTO for Report.
 */
public record ReportResponseDTO(
        Long reportId,
        String scope,
        String parametersJson,
        String metricsJson,
        LocalDateTime generatedAt,
        String reportUri
) {
}