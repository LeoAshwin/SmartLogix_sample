package com.cognizant.smartlogix.dto.OpsTrace.request;

public record CarrierAdapterRequestDTO(
        Long carrierId,
        String protocol,
        String credentialsJson,
        Boolean sandboxEnabled,
        String status
) {}