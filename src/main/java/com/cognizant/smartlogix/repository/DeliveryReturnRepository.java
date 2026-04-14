package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.DeliveryReturn;
import com.cognizant.smartlogix.model.enums.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryReturnRepository extends JpaRepository<DeliveryReturn, UUID> {
    List<DeliveryReturn> findByFulfillmentId(UUID fulfillmentId);
    Page<DeliveryReturn> findByStatus(ReturnStatus status, Pageable pageable);
}

