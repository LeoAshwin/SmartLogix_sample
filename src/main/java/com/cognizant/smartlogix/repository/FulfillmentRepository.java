package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Fulfillment;
import com.cognizant.smartlogix.model.data.FulfillmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Fulfillment entities.
 * Provides CRUD operations and commonly used query methods
 * required during order ingestion, validation, and processing.
 *
 */
@Repository
public interface FulfillmentRepository extends JpaRepository<Fulfillment, String> {

    /**
     * Fetch all fulfillments by their current status.
     */
    List<Fulfillment> findByStatus(FulfillmentStatus status);

    /**
     * Fetch all fulfillments for a given service zone.
     */
    List<Fulfillment> findByServiceZoneId(String serviceZoneId);
}