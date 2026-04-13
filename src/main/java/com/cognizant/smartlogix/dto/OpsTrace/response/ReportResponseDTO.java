

package com.cognizant.smartlogix.dto.OpsTrace.response;

public record ReportResponseDTO(
        Long reportId,
        String reportType,
        String generatedAt,
        String reportUri
) {}
