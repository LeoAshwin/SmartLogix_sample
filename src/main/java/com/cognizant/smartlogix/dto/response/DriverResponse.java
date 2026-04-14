package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.DriverStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DriverResponse {
    private UUID id;
    private UUID userId;
    private String driverName;
    private String licenseNumber;
    private String phone;
    private String shiftScheduleJson;
    private BigDecimal maxDailyHours;
    private DriverStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

