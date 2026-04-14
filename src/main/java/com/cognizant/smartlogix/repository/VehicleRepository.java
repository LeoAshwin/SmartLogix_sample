package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.Vehicle;
import com.cognizant.smartlogix.model.enums.VehicleStatus;
import com.cognizant.smartlogix.model.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumber(String registrationNumber);
    List<Vehicle> findByStatus(VehicleStatus status);
    Page<Vehicle> findByTypeAndStatus(VehicleType type, VehicleStatus status, Pageable pageable);
}

