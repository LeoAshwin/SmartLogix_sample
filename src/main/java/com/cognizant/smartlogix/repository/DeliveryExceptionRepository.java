package com.cognizant.smartlogix.repository;
import com.cognizant.smartlogix.model.DeliveryException;
import com.cognizant.smartlogix.model.data.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface DeliveryExceptionRepository extends JpaRepository<DeliveryException, Long> {

    List<DeliveryException> findByStatusOrderByRaisedAtDesc(DeliveryStatus status);

    List<DeliveryException> findByFulfillmentIdOrderByRaisedAtDesc(String fulfillmentId);

    @Query("SELECT e FROM DeliveryException e WHERE e.status = :status AND e.retryCount >= :threshold")
    List<DeliveryException> findHighRiskExceptions(DeliveryStatus status, int threshold);
}