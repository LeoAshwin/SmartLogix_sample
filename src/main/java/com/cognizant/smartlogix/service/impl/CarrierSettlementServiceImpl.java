package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.CarrierSettlementMapper;
import com.cognizant.smartlogix.dto.request.GenerateSettlementRequest;
import com.cognizant.smartlogix.dto.response.CarrierSettlementResponse;
import com.cognizant.smartlogix.exception.BusinessException;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.Carrier;
import com.cognizant.smartlogix.model.entity.CarrierBooking;
import com.cognizant.smartlogix.model.entity.CarrierSettlement;
import com.cognizant.smartlogix.model.enums.CarrierBookingStatus;
import com.cognizant.smartlogix.model.enums.CarrierSettlementStatus;
import com.cognizant.smartlogix.repository.CarrierBookingRepository;
import com.cognizant.smartlogix.repository.CarrierRepository;
import com.cognizant.smartlogix.repository.CarrierSettlementRepository;
import com.cognizant.smartlogix.service.CarrierSettlementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CarrierSettlementServiceImpl implements CarrierSettlementService {

    /** Commission rate applied to gross billed (deterministic rule) */
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.08");

    private final CarrierSettlementRepository settlementRepository;
    private final CarrierBookingRepository bookingRepository;
    private final CarrierRepository carrierRepository;
    private final CarrierSettlementMapper settlementMapper;

    public CarrierSettlementServiceImpl(CarrierSettlementRepository settlementRepository,
                                         CarrierBookingRepository bookingRepository,
                                         CarrierRepository carrierRepository,
                                         CarrierSettlementMapper settlementMapper) {
        this.settlementRepository = settlementRepository;
        this.bookingRepository    = bookingRepository;
        this.carrierRepository    = carrierRepository;
        this.settlementMapper     = settlementMapper;
    }

    @Override
    public CarrierSettlementResponse generate(GenerateSettlementRequest request) {
        // Prevent duplicate settlement for same carrier+period
        if (settlementRepository.findByCarrierIdAndPeriodStartAndPeriodEnd(
                request.getCarrierId(), request.getPeriodStart(), request.getPeriodEnd()).isPresent()) {
            throw new DuplicateResourceException(
                    "Settlement already exists for carrier " + request.getCarrierId()
                            + " period " + request.getPeriodStart() + " to " + request.getPeriodEnd());
        }

        Carrier carrier = carrierRepository.findById(request.getCarrierId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrier", "id", request.getCarrierId()));

        // Sum DELIVERED booking fees for carrier in period
        List<CarrierBooking> bookings = bookingRepository.findByCarrierId(request.getCarrierId())
                .stream()
                .filter(b -> b.getStatus() == CarrierBookingStatus.DELIVERED)
                .filter(b -> !b.getBookedAt().toLocalDate().isBefore(request.getPeriodStart()))
                .filter(b -> !b.getBookedAt().toLocalDate().isAfter(request.getPeriodEnd()))
                .toList();

        BigDecimal grossBilled = bookings.stream()
                .map(CarrierBooking::getFeeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal carrierFees  = grossBilled;
        BigDecimal commissions  = grossBilled.multiply(COMMISSION_RATE);
        BigDecimal netPayable   = carrierFees.subtract(commissions);

        CarrierSettlement settlement = CarrierSettlement.builder()
                .carrier(carrier)
                .periodStart(request.getPeriodStart())
                .periodEnd(request.getPeriodEnd())
                .grossBilled(grossBilled)
                .carrierFees(carrierFees)
                .commissions(commissions)
                .netPayable(netPayable)
                .generatedAt(LocalDateTime.now())
                .status(CarrierSettlementStatus.DRAFT)
                .build();

        return settlementMapper.toResponse(settlementRepository.save(settlement));
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierSettlementResponse findById(UUID id) {
        return settlementMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierSettlementResponse> findByCarrier(UUID carrierId, Pageable pageable) {
        return settlementRepository.findByCarrierId(carrierId, pageable).map(settlementMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarrierSettlementResponse> findByStatus(CarrierSettlementStatus status, Pageable pageable) {
        return settlementRepository.findByStatus(status, pageable).map(settlementMapper::toResponse);
    }

    @Override
    public CarrierSettlementResponse approve(UUID id) {
        return transition(id, CarrierSettlementStatus.SUBMITTED, CarrierSettlementStatus.APPROVED);
    }

    @Override
    public CarrierSettlementResponse dispute(UUID id, String discrepancyJson) {
        CarrierSettlement s = findEntityById(id);
        s.setStatus(CarrierSettlementStatus.DISPUTED);
        s.setDiscrepanciesJson(discrepancyJson);
        return settlementMapper.toResponse(settlementRepository.save(s));
    }

    @Override
    public CarrierSettlementResponse markPaid(UUID id) {
        return transition(id, CarrierSettlementStatus.APPROVED, CarrierSettlementStatus.PAID);
    }

    private CarrierSettlementResponse transition(UUID id, CarrierSettlementStatus expected,
                                                  CarrierSettlementStatus next) {
        CarrierSettlement s = findEntityById(id);
        if (s.getStatus() != expected) {
            throw new BusinessException("Settlement must be " + expected + " to transition to " + next);
        }
        s.setStatus(next);
        return settlementMapper.toResponse(settlementRepository.save(s));
    }

    private CarrierSettlement findEntityById(UUID id) {
        return settlementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarrierSettlement", "id", id));
    }
}

