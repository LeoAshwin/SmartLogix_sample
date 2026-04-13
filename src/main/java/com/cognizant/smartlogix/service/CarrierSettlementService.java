package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.pricing.CarrierSettlementCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.CarrierSettlementResponse;

import java.util.List;

public interface CarrierSettlementService {


    CarrierSettlementResponse createSettlement(
            CarrierSettlementCreateRequest request);


    List<CarrierSettlementResponse> getCarrierSettlements(Long carrierId);
}