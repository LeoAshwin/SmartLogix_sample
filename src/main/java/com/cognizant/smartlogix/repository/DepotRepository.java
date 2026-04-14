package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.Depot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepotRepository extends JpaRepository<Depot, UUID> {
    Page<Depot> findByStatus(String status, Pageable pageable);
    boolean existsByName(String name);
}

