package com.cognizant.smartlogix.dto.FleetSec;

import com.cognizant.smartlogix.model.data.DriverStatus;

public record DriverRequest(
        String licenseNumber,
        String phone,
        String shiftScheduleJson,
        int maxDailyHours,
        DriverStatus status
) {}
