package com.cognizant.smartlogix.dto.manifest;

import java.util.List;


public record ManifestResponseDTO(
        Long manifestId,
        Long vehicleId,
        String status,
        String scheduledDate,
        List<StopDTO> stops,
        Double totalDistance,
        String totalTime
) {}