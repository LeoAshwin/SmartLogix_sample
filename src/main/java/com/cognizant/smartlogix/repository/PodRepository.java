package com.cognizant.smartlogix.repository;
import com.cognizant.smartlogix.model.Pod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PodRepository extends JpaRepository<Pod, Long> {

    // Check if a POD already exists before saving (Idempotency check)
    boolean existsByFulfillmentId(Long fulfillmentId);

    // Find POD by Fulfillment ID for the Merchant Portal (Module 4.10)
    Optional<Pod> findByFulfillmentId(Long fulfillmentId);

    // Find all PODs delivered by a specific driver for settlement (Module 4.8)
    List<Pod> findByDeliveredBy(Long driverId);
}