package com.cognizant.smartlogix.dto.Driver;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDetails {
    @NotNull(message = "Latitude is required for tracking")
    private Double latitude;

    @NotNull(message = "Longitude is required for tracking")
    private Double longitude;

    private String address;

}