package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CarrierRepository extends JpaRepository<Carrier, UUID> {
}