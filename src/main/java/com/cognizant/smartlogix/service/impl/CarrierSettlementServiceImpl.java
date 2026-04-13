package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.pricing.CarrierSettlementCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.model.CarrierSettlement;
import com.cognizant.smartlogix.repository.CarrierSettlementRepository;
import com.cognizant.smartlogix.service.CarrierSettlementService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierSettlementServiceImpl
        implements CarrierSettlementService {

    private final CarrierSettlementRepository carrierSettlementRepository;

    public CarrierSettlementServiceImpl(
            CarrierSettlementRepository carrierSettlementRepository) {
        this.carrierSettlementRepository = carrierSettlementRepository;
    }

    @Override
    public CarrierSettlementResponse createSettlement(
            CarrierSettlementCreateRequest request) {

        CarrierSettlement settlement = new CarrierSettlement();
        settlement.setCarrierId(request.carrierId());
        settlement.setPeriodStart(request.periodStart());
        settlement.setPeriodEnd(request.periodEnd());
        settlement.setGrossBilled(request.grossBilled());
        settlement.setCarrierFees(request.carrierFees());
        settlement.setCommissions(request.commissions());
        settlement.setNetPayable(request.netPayable());
        settlement.setDiscrepanciesJson(request.discrepanciesJson());
        settlement.setGeneratedAt(request.generatedAt());
        settlement.setStatus(request.status());

        CarrierSettlement saved =
                carrierSettlementRepository.save(settlement);

        return map(saved);
    }

    @Override
    public List<CarrierSettlementResponse> getCarrierSettlements(
            Long carrierId) {

        return carrierSettlementRepository.findByCarrierId(carrierId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private CarrierSettlementResponse map(CarrierSettlement settlement) {
        return new CarrierSettlementResponse(
                settlement.getSettleId(),
                settlement.getCarrierId(),
                settlement.getPeriodStart(),
                settlement.getPeriodEnd(),
                settlement.getGrossBilled(),
                settlement.getCarrierFees(),
                settlement.getCommissions(),
                settlement.getNetPayable(),
                settlement.getDiscrepanciesJson(),
                settlement.getGeneratedAt(),
                settlement.getStatus()
        );
    }
}