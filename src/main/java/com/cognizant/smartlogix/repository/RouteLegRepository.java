package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.RouteLeg;
import com.cognizant.smartlogix.model.enums.RouteLegStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteLegRepository extends JpaRepository<RouteLeg, UUID> {
    List<RouteLeg> findByManifestIdOrderBySequence(UUID manifestId);
    List<RouteLeg> findByManifestIdAndStatus(UUID manifestId, RouteLegStatus status);
}

