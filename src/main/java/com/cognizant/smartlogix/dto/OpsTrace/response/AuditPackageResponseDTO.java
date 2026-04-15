package com.cognizant.smartlogix.dto.OpsTrace.response;

import java.time.LocalDateTime;

public record AuditPackageResponseDTO(
        Long packageId,
        LocalDateTime periodStart,
        LocalDateTime periodEnd,
        String contentsJson,
        LocalDateTime generatedAt,
        String packageUri
) {
}