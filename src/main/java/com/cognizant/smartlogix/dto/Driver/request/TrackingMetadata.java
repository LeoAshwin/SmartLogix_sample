package com.cognizant.smartlogix.dto.Driver.request;

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

     String appVersion
) {

    public TrackingMetadata {
        if (isOfflineSync == null) {
            isOfflineSync = false;
        }
    }
}