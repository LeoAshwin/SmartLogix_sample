package com.cognizant.smartlogix.dto.OpsTrace.request;

public record ReportRequestDTO(
        String reportType,
        String generatedAt,
        String reportUri
) {}