package com.cognizant.smartlogix.dto.manifest;

/**
 * Data Transfer Object representing a specific segment between two locations in a manifest.
 * Encapsulates the sequence, travel metrics, and current status of a single trip leg.
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