package com.cognizant.smartlogix.dto.manifest;

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