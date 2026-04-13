package com.cognizant.smartlogix.dto.manifest;

/**
 * Data Transfer Object representing a specific delivery point within a manifest's route.
 * Contains sequencing, geospatial data, and time-tracking metrics for fulfillment.
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