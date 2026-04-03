package com.cognizant.smartlogix.dto.manifest;

import java.time.LocalDateTime;

/**
 * Record for Order Input data.
 * Records automatically provide a constructor, getters, equals, hashCode, and toString.
 */
public record OrderInputDTO(
        Long orderId,
        Double lat,
        Double lng,
        Double weight,
        LocalDateTime deliveryWindowStart
) {}