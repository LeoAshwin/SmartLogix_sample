package com.cognizant.smartlogix.dto.OpsTrace.request;

import java.time.LocalDateTime;

/**
 * Immutable request DTO (record-style).
 */
public record AuditPackageRequestDTO(
        LocalDateTime periodStart,
        LocalDateTime periodEnd,
        String contentsJson,
        String packageUri
) {
}
