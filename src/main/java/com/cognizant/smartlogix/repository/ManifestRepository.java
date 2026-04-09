package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Manifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access layer for Manifest entities.
 * Provides abstracted query methods for retrieving logistics data from the MySQL database.
 */
@Repository
public interface ManifestRepository extends JpaRepository<Manifest, Long> {

    /** Filters manifests by their current lifecycle status (e.g., 'DISPATCHED', 'COMPLETED'). */
    List<Manifest> findByStatus(String status);

    /** Fetches all manifest records scheduled for a specific date. */
    List<Manifest> findByDate(LocalDate date);

    /** Retrieves history of manifests assigned to a specific vehicle. */
    List<Manifest> findByVehicleId(Long vehicleId);

    /** Retrieves history of manifests assigned to a specific driver. */
    List<Manifest> findByDriverId(Long driverId);
}