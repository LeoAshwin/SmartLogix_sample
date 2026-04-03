package com.cognizant.smartlogix.dto.manifest;

/**
 * Record for Stop data.
 * This represents a single delivery location within a Manifest.
 */
public record StopDTO(
        Long fulfillmentId,
        Integer sequence,
        String estimatedArrivalTime,
        String handlingInstructions,
        Double latitude,
        Double longitude,
        String status,
        String actualArrivalTime
) {}