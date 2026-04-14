package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.Driver;
import com.cognizant.smartlogix.model.enums.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    boolean existsByLicenseNumber(String licenseNumber);
    List<Driver> findByStatus(DriverStatus status);
    Page<Driver> findByStatus(DriverStatus status, Pageable pageable);
    Optional<Driver> findByUserId(UUID userId);
}

