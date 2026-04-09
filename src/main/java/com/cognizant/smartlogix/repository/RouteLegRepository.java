package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.RouteLeg;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access layer for RouteLeg entities.
 * Manages the individual segments of a manifest route, including sequencing and execution status.
 */
@Repository
public interface RouteLegRepository extends JpaRepository<RouteLeg, Long> {

    /**
     * Retrieves all legs associated with a manifest, sorted by their travel sequence.
     * Essential for reconstructing the chronological path of a trip.
     */
    List<RouteLeg> findByManifestIdOrderBySequenceAsc(Long manifestId);

    /**
     * Removes all route segments linked to a specific manifest.
     * Primarily used during manifest cancellation or re-optimization phases.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM RouteLeg r WHERE r.manifestId = :manifestId")
    void deleteByManifestId(Long manifestId);

    /**
     * Updates the status of a specific segment within a manifest's route.
     * Allows for granular tracking of progress (e.g., marking a leg as 'COMPLETED').
     */
    @Modifying
    @Transactional
    @Query("UPDATE RouteLeg r SET r.status = :status WHERE r.manifestId = :manifestId AND r.sequence = :sequence")
    void updateStatusByManifestAndSequence(Long manifestId, Integer sequence, String status);

    /**
     * Fetches all legs for a manifest without a specific sort order.
     */
    List<RouteLeg> findByManifestId(Long manifestId);
}