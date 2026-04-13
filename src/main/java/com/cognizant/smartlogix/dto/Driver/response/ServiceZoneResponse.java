package com.cognizant.smartlogix.dto.Driver.response;

/**
 * Response DTO for service zone creation.
 */
public record ServiceZoneResponse(
        String zoneId,
        String name,
        String status
) {
}
