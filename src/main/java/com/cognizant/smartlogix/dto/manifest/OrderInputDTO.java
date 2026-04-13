package com.cognizant.smartlogix.dto.manifest;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing an individual order to be included in a manifest.
 * Contains geospatial coordinates and delivery constraints for route optimization.
 */
public record OrderInputDTO(
        Long orderId,
        Double lat,
        Double lng,
        Double weight,
        LocalDateTime deliveryWindowStart
) {}