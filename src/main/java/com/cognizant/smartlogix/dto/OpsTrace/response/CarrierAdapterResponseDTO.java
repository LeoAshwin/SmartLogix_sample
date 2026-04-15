package com.cognizant.smartlogix.dto.OpsTrace.response;

import java.time.LocalDateTime;

/**
 * Immutable response DTO for Carrier Adapter.
 */
public record CarrierAdapterResponseDTO(
        Long adapterId,
        Long carrierId,
        String protocol,
        String credentialsJson,
        LocalDateTime lastSyncAt,
        String status
) {
}