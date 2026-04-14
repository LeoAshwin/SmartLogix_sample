package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateVehicleRequest {

    @NotNull(message = "Vehicle type is required")
    private String type;

    @NotNull(message = "Capacity (kg) is required")
    @Positive
    private BigDecimal capacityKg;

    @NotNull(message = "Capacity (mÂ³) is required")
    @Positive
    private BigDecimal capacityVolumeM3;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    private UUID fleetId;
}

