package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.GenerateSettlementRequest;
import com.cognizant.smartlogix.dto.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.model.enums.CarrierSettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CarrierSettlementService {

    /**
     * Deterministic settlement generation:
     * Sum all DELIVERED CarrierBooking fees for carrier in period,
     * apply commission rules, capture discrepancies.
     */
    CarrierSettlementResponse generate(GenerateSettlementRequest request);

    CarrierSettlementResponse findById(UUID id);

    Page<CarrierSettlementResponse> findByCarrier(UUID carrierId, Pageable pageable);

    Page<CarrierSettlementResponse> findByStatus(CarrierSettlementStatus status, Pageable pageable);

    CarrierSettlementResponse approve(UUID id);

    CarrierSettlementResponse dispute(UUID id, String discrepancyJson);

    CarrierSettlementResponse markPaid(UUID id);
}

