package com.cognizant.smartlogix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateCarrierRequest {

    @NotBlank(message = "Carrier name is required")
    private String name;

    private String contractTermsJson;

    private String allowedZonesJson;

    @Positive
    private BigDecimal maxWeightKg;
}

