package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.ServiceZoneStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ServiceZoneResponse {
    private UUID id;
    private String name;
    private String polygonGeoJson;
    private String postalCodesJson;
    private String slaConfigJson;
    private Integer capacityPerSlot;
    private String timeZone;
    private ServiceZoneStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

