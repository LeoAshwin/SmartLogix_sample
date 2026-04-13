package com.cognizant.smartlogix.dto.manifest;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object representing the initial request to generate a manifest.
 * Encapsulates depot constraints, vehicle capacity, and the list of orders to be routed.
 */
public record ManifestRequestDTO(
        Long depotId,
        Long vehicleId,
        Long driverId,
        LocalDate scheduledDate,
        Double maxCapacityKg,
        Double averageSpeedKmH,
        List<OrderInputDTO> orders
) {
    /**
     * Note: As a Java Record, this component is immutable and thread-safe,
     * making it ideal for passing request data through the Service layer.
     */
}