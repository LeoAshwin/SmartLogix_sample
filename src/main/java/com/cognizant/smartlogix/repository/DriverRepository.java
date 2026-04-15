package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
}
