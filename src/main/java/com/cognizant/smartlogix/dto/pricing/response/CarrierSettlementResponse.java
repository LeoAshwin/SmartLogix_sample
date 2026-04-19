package com.cognizant.smartlogix.dto.pricing.response;

import com.cognizant.smartlogix.model.data.CarrierSettlementStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CarrierSettlementResponse(
        Long settleId,
        String carrierId,
        LocalDateTime periodStart,
        LocalDateTime periodEnd,
        BigDecimal grossBilled,
        BigDecimal carrierFees,
        BigDecimal commissions,
        BigDecimal netPayable,
        String discrepanciesJson,
        LocalDateTime generatedAt,
        CarrierSettlementStatus status
) {
}