package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.Fulfillment;
import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import com.cognizant.smartlogix.model.enums.ServiceLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FulfillmentRepository extends JpaRepository<Fulfillment, UUID> {

    Optional<Fulfillment> findByOrderId(String orderId);
    boolean existsByOrderId(String orderId);

    Page<Fulfillment> findByStatus(FulfillmentStatus status, Pageable pageable);
    Page<Fulfillment> findByMerchantId(UUID merchantId, Pageable pageable);
    Page<Fulfillment> findByServiceZoneId(UUID serviceZoneId, Pageable pageable);

    List<Fulfillment> findByStatusAndServiceZoneId(FulfillmentStatus status, UUID serviceZoneId);

    @Query("""
           SELECT f FROM Fulfillment f
           WHERE f.status = :status
             AND f.serviceZone.id = :zoneId
             AND f.deliveryWindowStart >= :windowStart
             AND f.deliveryWindowEnd <= :windowEnd
           ORDER BY f.serviceLevel, f.deliveryWindowStart
           """)
    List<Fulfillment> findEligibleForManifest(
            @Param("status") FulfillmentStatus status,
            @Param("zoneId") UUID zoneId,
            @Param("windowStart") LocalDateTime windowStart,
            @Param("windowEnd") LocalDateTime windowEnd);

    Page<Fulfillment> findByMerchantIdAndStatus(UUID merchantId, FulfillmentStatus status, Pageable pageable);
}

