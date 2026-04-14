package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.CarrierStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CarrierResponse {
    private UUID id;
    private String name;
    private String contractTermsJson;
    private String allowedZonesJson;
    private BigDecimal maxWeightKg;
    private CarrierStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

