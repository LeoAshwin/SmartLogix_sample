package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.CarrierSettlement;
import com.cognizant.smartlogix.model.enums.CarrierSettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrierSettlementRepository extends JpaRepository<CarrierSettlement, UUID> {
    Page<CarrierSettlement> findByCarrierId(UUID carrierId, Pageable pageable);
    Page<CarrierSettlement> findByStatus(CarrierSettlementStatus status, Pageable pageable);
    Optional<CarrierSettlement> findByCarrierIdAndPeriodStartAndPeriodEnd(
            UUID carrierId, LocalDate periodStart, LocalDate periodEnd);
    List<CarrierSettlement> findByCarrierIdAndStatus(UUID carrierId, CarrierSettlementStatus status);
}

