package com.cognizant.smartlogix.dto.OpsTrace.response;

public record AuditPackageResponseDTO(
        Long packageId,
        String periodStart,
        String periodEnd,
        String generatedAt,
        String packageUri
) {}