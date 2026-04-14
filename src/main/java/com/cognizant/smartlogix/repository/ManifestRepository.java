package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.Manifest;
import com.cognizant.smartlogix.model.enums.ManifestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ManifestRepository extends JpaRepository<Manifest, UUID> {
    List<Manifest> findByDepotIdAndDate(UUID depotId, LocalDate date);
    List<Manifest> findByDriverIdAndDate(UUID driverId, LocalDate date);
    List<Manifest> findByVehicleIdAndDate(UUID vehicleId, LocalDate date);
    Page<Manifest> findByStatus(ManifestStatus status, Pageable pageable);
    Page<Manifest> findByDepotIdAndDateBetween(UUID depotId, LocalDate from, LocalDate to, Pageable pageable);
}

