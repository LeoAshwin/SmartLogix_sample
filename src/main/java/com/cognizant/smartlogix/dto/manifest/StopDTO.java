package com.cognizant.smartlogix.dto.manifest;

import lombok.Data;

@Data
public class StopDTO {
    private Long fulfillmentId;
    private Integer sequence;
    private String estimatedArrivalTime; // Deterministic ETA
    private String handlingInstructions;

    // ADD THESE TWO FIELDS:
    private Double latitude;
    private Double longitude;

    private String status; // To track if the stop is PENDING or COMPLETED
    private String actualArrivalTime;
}