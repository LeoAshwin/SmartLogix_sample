package com.cognizant.smartlogix.dto.OpsTrace.request;

/**
 * Immutable request DTO for Carrier Adapter.
 */
public record CarrierAdapterRequestDTO(
        Long carrierId,
        String protocol,
        String credentialsJson,
        String status
) {
}