package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.CarrierBooking;
import com.cognizant.smartlogix.model.enums.CarrierBookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrierBookingRepository extends JpaRepository<CarrierBooking, UUID> {
    Optional<CarrierBooking> findByFulfillmentId(UUID fulfillmentId);
    List<CarrierBooking> findByCarrierId(UUID carrierId);
    Page<CarrierBooking> findByCarrierIdAndStatus(UUID carrierId, CarrierBookingStatus status, Pageable pageable);
    boolean existsByFulfillmentId(UUID fulfillmentId);
}

