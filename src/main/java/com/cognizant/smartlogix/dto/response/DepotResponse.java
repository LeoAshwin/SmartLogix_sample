package com.cognizant.smartlogix.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DepotResponse {
    private UUID id;
    private String name;
    private String addressJson;
    private String timeZone;
    private String capacityJson;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

