package com.cognizant.smartlogix.dto.OpsTrace.request;

public record AuditPackageRequestDTO(
        String periodStart,
        String periodEnd,
        String generatedAt,
        String packageUri
) {}