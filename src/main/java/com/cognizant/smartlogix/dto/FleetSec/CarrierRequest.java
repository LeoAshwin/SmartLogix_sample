package com.cognizant.smartlogix.dto.FleetSec;

import com.cognizant.smartlogix.model.data.CarrierStatus;

public record CarrierRequest(
        String name,
        String contractTermsJson,
        String allowedZonesJson,
        double maxWeightKg,
        CarrierStatus status
) {}
