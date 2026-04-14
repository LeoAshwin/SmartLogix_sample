package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.ProofOfDelivery;
import com.cognizant.smartlogix.model.enums.PODStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProofOfDeliveryRepository extends JpaRepository<ProofOfDelivery, UUID> {
    Optional<ProofOfDelivery> findByFulfillmentId(UUID fulfillmentId);
    boolean existsByFulfillmentId(UUID fulfillmentId);
}

