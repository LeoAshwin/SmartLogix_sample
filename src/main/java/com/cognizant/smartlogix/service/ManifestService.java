package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface defining the core business logic for Manifest management.
 * Coordinates the transition between planning, optimization, and real-time execution.
 */
public interface ManifestService {

    /**
     * Requirement 4.4: Generates a deterministic manifest based on depot constraints,
     * scheduled date, and vehicle capacity.
     */
    ManifestResponseDTO generateDeterministicManifest(ManifestRequestDTO request);

    /** Retrieves detailed manifest information by its primary identifier. */
    ManifestResponseDTO getManifestById(Long manifestId);

    /** Finalizes the planning phase and transitions the manifest status to DISPATCHED. */
    ManifestResponseDTO dispatchManifest(Long manifestId);

    /** Permanently removes a manifest record from the system. */
    void deleteManifest(Long manifestId);

    /** Marks the official start of a delivery trip, enabling real-time tracking. */
    ManifestResponseDTO startTrip(Long manifestId);

    /** * Records the successful completion of a specific stop within a manifest.
     * Triggers status updates for the associated fulfillment record.
     */
    ManifestResponseDTO markStopAsCompleted(Long manifestId, Long fulfillmentId);

    /** Provides dynamic filtering of manifests based on status, driver, or date. */
    List<ManifestResponseDTO> searchManifests(String status, Long driverId, LocalDate date);

    /** Voids an active manifest and releases assigned resources. */
    void cancelManifest(Long manifestId);

    /** Generates a byte-array representation of the manifest as a PDF trip sheet. */
    byte[] exportManifestToPdf(Long id);

    /**
     * Requirement 4.4: Updates the stop sequence based on manual dispatcher overrides.
     * @param manifestId The ID of the manifest to modify.
     * @param stops The new ordered list of stops.
     */
    ManifestResponseDTO updateManifestManualOverride(Long manifestId, List<StopDTO> stops);

    /** Fetches a collection of manifests currently in a specific lifecycle state. */
    List<ManifestResponseDTO> getManifestsByStatus(String status);

    /** Retrieves all manifest history associated with a specific vehicle. */
    List<ManifestResponseDTO> getManifestsByVehicle(Long vehicleId);
}