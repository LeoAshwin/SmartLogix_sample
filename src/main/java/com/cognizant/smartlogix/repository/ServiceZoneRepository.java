package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.ServiceZone;
import com.cognizant.smartlogix.model.data.ServiceZoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing ServiceZone entities.
 * Handles persistence and retrieval of service zone definitions
 * including SLA rules, capacity constraints, and operational status.
 *
 */
@Repository
public interface ServiceZoneRepository extends JpaRepository<ServiceZone, String> {

    /**
     * Find service zones by operational status.
     */
    List<ServiceZone> findByStatus(ServiceZoneStatus status);

    /**
     * Find service zone by name.
     */
    Optional<ServiceZone> findByName(String name);
}
