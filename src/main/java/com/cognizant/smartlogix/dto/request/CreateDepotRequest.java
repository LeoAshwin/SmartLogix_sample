package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDepotRequest {

    @NotBlank(message = "Depot name is required")
    private String name;

    @NotBlank(message = "Address JSON is required")
    private String addressJson;

    @NotBlank(message = "Timezone is required")
    private String timeZone;

    private String capacityJson;
}

