package com.cognizant.smartlogix.dto.manifest;

import java.util.List;

/**
 * Modern Java Record for the Manifest Response.
 * No more @Data or private fields needed!
 */
public record ManifestResponseDTO(
        Long manifestId,
        Long vehicleId,
        String status,
        String scheduledDate,
        List<StopDTO> stops,
        Double totalDistance
) {}