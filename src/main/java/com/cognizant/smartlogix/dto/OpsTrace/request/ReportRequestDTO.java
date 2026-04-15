package com.cognizant.smartlogix.dto.OpsTrace.request;

/**
 * Immutable request DTO for creating/updating a Report.
 */
public record ReportRequestDTO(
        String scope,
        String parametersJson,
        String reportUri
) {
}