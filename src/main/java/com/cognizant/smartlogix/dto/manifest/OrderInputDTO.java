package com.cognizant.smartlogix.dto.manifest;

import java.time.LocalDateTime;

public record OrderInputDTO(
        Long orderId,
        Double lat,
        Double lng,
        Double weight,
        LocalDateTime deliveryWindowStart
) {}