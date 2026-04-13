package com.cognizant.smartlogix.dto.Manager;

/**
 * Request DTO for creating a service zone.
 */
public record ServiceZoneRequest(
        String name,
        String polygonGeoJson,
        String postalCodesJson,
        String slaConfigJson,
        Integer capacityPerSlot,
        String timeZone
) {
}