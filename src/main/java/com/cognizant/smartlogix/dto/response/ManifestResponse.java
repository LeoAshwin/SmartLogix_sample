package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.ManifestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ManifestResponse {
    private UUID id;
    private UUID depotId;
    private String depotName;
    private UUID vehicleId;
    private String vehicleRegistration;
    private UUID driverId;
    private String driverName;
    private LocalDate date;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String stopsJson;
    private ManifestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

