package com.cognizant.smartlogix.service;



/**
 * Service interface for managing individual segments (legs) of a manifest route.
 * Handles the calculation, storage, and retrieval of trip sequences.
 */
public interface RouteLegService {

    /**
     * Requirement 4.4: Deterministically generates a sequence of route legs
     * based on the provided manifest ID and serialized stop data.
     */
    void generateLegsForManifest(Long manifestId, String stopsJson);

    /**
     * Removes all route segments linked to a specific manifest.
     * Often used during re-optimization or cancellation of a trip.
     */
    void deleteByManifestId(Long manifestId);
}