package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Manifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ManifestRepository extends JpaRepository<Manifest, Long> {

    List<Manifest> findByStatus(String status);

    List<Manifest> findByDate(LocalDate date);

    List<Manifest> findByVehicleId(String vehicleId);

    List<Manifest> findByDriverId(String driverId);
}