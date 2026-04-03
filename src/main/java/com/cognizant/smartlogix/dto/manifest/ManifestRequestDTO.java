package com.cognizant.smartlogix.dto.manifest;

import java.time.LocalDate;
import java.util.List;

/**
 * Record for Manifest Requests.
 * Records are immutable by default and handle boilerplate (getters/toString) automatically.
 */
public record ManifestRequestDTO(
        Long depotId,
        Long vehicleId,
        Long driverId,
        LocalDate scheduledDate,
        Double maxCapacityKg,
        Double averageSpeedKmH,
        List<OrderInputDTO> orders
) {}