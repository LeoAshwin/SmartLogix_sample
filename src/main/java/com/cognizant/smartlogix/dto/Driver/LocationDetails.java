package com.cognizant.smartlogix.dto.Driver;

import jakarta.validation.constraints.NotNull;

public record LocationDetails(
        @NotNull(message = "Latitude is required for tracking")
        Double latitude,

        @NotNull(message = "Longitude is required for tracking")
        Double longitude,

        String address
) {

}