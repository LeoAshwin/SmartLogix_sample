package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.CarrierBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarrierBookingRepository
        extends JpaRepository<CarrierBooking, Long> {

    List<CarrierBooking> findByCarrierId(Long carrierId);
}