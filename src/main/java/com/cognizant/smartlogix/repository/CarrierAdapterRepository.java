package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.CarrierAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarrierAdapterRepository extends JpaRepository<CarrierAdapter, UUID> {
    List<CarrierAdapter> findByCarrierId(UUID carrierId);
    Page<CarrierAdapter> findByCarrierId(UUID carrierId, Pageable pageable);
}

