package com.cognizant.smartlogix.dto.manifest;

import java.util.List;

/**
 * Data Transfer Object representing the finalized state of a logistics manifest.
 * Used to transmit optimized routing details and current execution status to the frontend.
 */
public record ManifestResponseDTO(
        Long manifestId,
        Long vehicleId,
        String status,
        String scheduledDate,
        List<StopDTO> stops,
        Double totalDistance,
        String totalTime
) {}