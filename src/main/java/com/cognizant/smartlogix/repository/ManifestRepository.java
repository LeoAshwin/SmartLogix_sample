package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Manifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ManifestRepository extends JpaRepository<Manifest, Long> {
    // This finds all manifests for a specific depot on a specific date
    List<Manifest> findByDepotIdAndDate(Long depotId, LocalDate date);
    // Filter by Status (e.g., COMPLETED, STARTED)
    List<Manifest> findByStatus(String status);

    // Filter by Driver and Date
    List<Manifest> findByDriverIdAndDate(Long driverId, LocalDate date);

    // Find all manifests for a specific date (Today's trips)
    List<Manifest> findByDate(LocalDate date);

    // Only find manifests that are NOT cancelled
    @Query("SELECT m FROM Manifest m WHERE m.status != 'CANCELLED'")
    List<Manifest> findAllActiveManifests();

    List<Manifest> findByVehicleId(Long vehicleId);
    List<Manifest> findByDriverId(Long driverId);
}