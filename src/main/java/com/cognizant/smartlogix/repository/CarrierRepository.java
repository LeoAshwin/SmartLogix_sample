package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.Carrier;
import com.cognizant.smartlogix.model.enums.CarrierStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, UUID> {
    Page<Carrier> findByStatus(CarrierStatus status, Pageable pageable);
    boolean existsByName(String name);
}

