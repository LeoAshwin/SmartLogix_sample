package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateServiceZoneRequest {

    @NotBlank(message = "Zone name is required")
    private String name;

    private String polygonGeoJson;

    private String postalCodesJson;

    private String slaConfigJson;

    private Integer capacityPerSlot;

    @NotBlank(message = "Timezone is required")
    private String timeZone;
}

