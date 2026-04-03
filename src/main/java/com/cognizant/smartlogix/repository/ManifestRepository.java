package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Manifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ManifestRepository extends JpaRepository<Manifest, Long> {
    // This finds all manifests for a specific depot on a specific date
    List<Manifest> findByDepotIdAndDate(Long depotId, LocalDate date);
}