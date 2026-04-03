package com.cognizant.smartlogix.dto.manifest;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ManifestRequestDTO {
    private Long depotId;
    private Long vehicleId;
    private Long driverId;
    private LocalDate scheduledDate;

    // Heuristic parameters (Optional but good for 4.4 compliance)
    private Double maxCapacityKg;
    private Double averageSpeedKmH;
    // The list of orders to process
    private List<OrderInputDTO> orders;
}
