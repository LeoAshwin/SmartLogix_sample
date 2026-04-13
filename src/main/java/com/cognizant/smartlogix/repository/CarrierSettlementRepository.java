package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.CarrierSettlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarrierSettlementRepository
        extends JpaRepository<CarrierSettlement, Long> {

    List<CarrierSettlement> findByCarrierId(Long carrierId);
}
