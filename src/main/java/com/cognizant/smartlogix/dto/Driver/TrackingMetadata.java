package com.cognizant.smartlogix.dto.Driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrackingMetadata {

    @NotBlank(message = "Device ID is mandatory for audit trails")
    private String deviceId;

    private String batteryLevel;

    @NotNull
    private Boolean isOfflineSync = false;

    @NotBlank(message = "Original device timestamp is required for offline sync logic")
    private String deviceLocalTimestamp;

    private String appVersion;
}