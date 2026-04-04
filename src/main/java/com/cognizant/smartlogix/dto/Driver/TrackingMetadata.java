package com.cognizant.smartlogix.dto.Driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrackingMetadata(
        @NotBlank(message = "Device ID is mandatory for audit trails")
        String deviceId,

        String batteryLevel,

        @NotNull
        Boolean isOfflineSync,

        @NotBlank(message = "Original device timestamp is required for offline sync logic")
        String deviceLocalTimestamp,

     String appVersion // Note: Remove 'private' here as well
) {
    // You can add a compact constructor if you need default values
    public TrackingMetadata {
        if (isOfflineSync == null) {
            isOfflineSync = false;
        }
    }
}