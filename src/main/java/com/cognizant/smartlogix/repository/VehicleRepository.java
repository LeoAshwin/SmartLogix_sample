package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
}
