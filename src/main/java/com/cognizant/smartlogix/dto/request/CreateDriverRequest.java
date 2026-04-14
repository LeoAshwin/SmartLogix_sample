package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateDriverRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String shiftScheduleJson;

    @NotNull(message = "Max daily hours is required")
    @Positive
    private BigDecimal maxDailyHours;
}

