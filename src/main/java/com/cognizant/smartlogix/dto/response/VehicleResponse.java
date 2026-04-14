package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.VehicleStatus;
import com.cognizant.smartlogix.model.enums.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VehicleResponse {
    private UUID id;
    private UUID fleetId;
    private VehicleType type;
    private BigDecimal capacityKg;
    private BigDecimal capacityVolumeM3;
    private String registrationNumber;
    private VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

