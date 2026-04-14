package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.ServiceZone;
import com.cognizant.smartlogix.model.enums.ServiceZoneStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceZoneRepository extends JpaRepository<ServiceZone, UUID> {
    Page<ServiceZone> findByStatus(ServiceZoneStatus status, Pageable pageable);
    List<ServiceZone> findByStatus(ServiceZoneStatus status);
    boolean existsByName(String name);
}

