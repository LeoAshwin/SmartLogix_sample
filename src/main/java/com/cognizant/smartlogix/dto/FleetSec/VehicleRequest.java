package com.cognizant.smartlogix.dto.FleetSec;

import com.cognizant.smartlogix.model.data.VehicleStatus;
import com.cognizant.smartlogix.model.data.VehicleType;

public record VehicleRequest(
        VehicleType type,
        double capacityKg,
        double capacityVolumeM3,
        String registrationNumber,
        VehicleStatus status
) {}
