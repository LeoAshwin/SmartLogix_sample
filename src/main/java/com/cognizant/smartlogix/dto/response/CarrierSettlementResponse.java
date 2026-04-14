package com.cognizant.smartlogix.dto.response;

import com.cognizant.smartlogix.model.enums.CarrierSettlementStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CarrierSettlementResponse {
    private UUID id;
    private UUID carrierId;
    private String carrierName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal grossBilled;
    private BigDecimal carrierFees;
    private BigDecimal commissions;
    private BigDecimal netPayable;
    private String discrepanciesJson;
    private LocalDateTime generatedAt;
    private CarrierSettlementStatus status;
    private LocalDateTime createdAt;
}

