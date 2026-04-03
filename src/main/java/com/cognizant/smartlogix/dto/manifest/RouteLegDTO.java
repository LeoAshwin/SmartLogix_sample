package com.cognizant.smartlogix.dto.manifest;

/**
 * Record for Route Leg data.
 * Replaces the Lombok-heavy class with a clean, immutable structure.
 */
public record RouteLegDTO(
        Long legId,
        Long manifestId,
        Integer sequence,
        String fromLocation,
        String toLocation,
        Double distance,
        Integer duration,
        String status
) {}
