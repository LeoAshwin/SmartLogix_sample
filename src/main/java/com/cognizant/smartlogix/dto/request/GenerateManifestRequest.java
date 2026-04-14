package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class GenerateManifestRequest {

    @NotNull(message = "Depot ID is required")
    private UUID depotId;

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    @NotNull(message = "Driver ID is required")
    private UUID driverId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    /** Zone to include in this manifest */
    @NotNull(message = "Service zone ID is required")
    private UUID serviceZoneId;
}

