package com.cognizant.smartlogix.dto.OpsTrace.response;

public record CarrierAdapterResponseDTO(
        Long adapterId,
        Long carrierId,
        String protocol,
        Boolean sandboxEnabled,
        String status,
        String lastSyncAt
) {}