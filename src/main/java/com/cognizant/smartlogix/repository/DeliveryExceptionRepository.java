package com.cognizant.smartlogix.repository;
import com.cognizant.smartlogix.model.DeliveryException;
import com.cognizant.smartlogix.model.data.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface DeliveryExceptionRepository extends JpaRepository<DeliveryException, Long> {

    // Find all unresolved exceptions for the Dispatcher Dashboard
    List<DeliveryException> findByStatusOrderByRaisedAtDesc(DeliveryStatus status);

    // Find exceptions for a specific fulfillment to see reattempt history
    List<DeliveryException> findByFulfillmentIdOrderByRaisedAtDesc(Long fulfillmentId);

    // Custom query to find high-priority exceptions (Requirement 4.6: Escalation)
    @Query("SELECT e FROM DeliveryException e WHERE e.status = 'OPEN' AND e.retryCount > 2")
    List<DeliveryException> findExceptionsNeedingEscalation();
}