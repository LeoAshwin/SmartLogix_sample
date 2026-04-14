package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.DeliveryException;
import com.cognizant.smartlogix.model.enums.ExceptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryExceptionRepository extends JpaRepository<DeliveryException, UUID> {
    List<DeliveryException> findByFulfillmentId(UUID fulfillmentId);
    Page<DeliveryException> findByStatus(ExceptionStatus status, Pageable pageable);
    Page<DeliveryException> findByFulfillmentIdAndStatus(UUID fulfillmentId, ExceptionStatus status, Pageable pageable);
    long countByStatus(ExceptionStatus status);
}

