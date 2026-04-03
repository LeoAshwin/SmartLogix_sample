package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;

import java.time.LocalDate;
import java.util.List;

public interface ManifestService {
    /**
     * Requirement 4.4: Deterministic manifest generation
     * based on depot, date, and vehicle capacity.
     */
    ManifestResponseDTO generateDeterministicManifest(ManifestRequestDTO request);
    ManifestResponseDTO getManifestById(Long manifestId);
    ManifestResponseDTO dispatchManifest(Long manifestId);
    void deleteManifest(Long manifestId);
    public ManifestResponseDTO startTrip(Long manifestId);
    public ManifestResponseDTO markStopAsCompleted(Long manifestId, Long fulfillmentId);
    List<ManifestResponseDTO> searchManifests(String status, Long driverId, LocalDate date);
    void cancelManifest(Long manifestId);
    /**
     * Requirement 4.4: Support manual dispatcher overrides.
     */
    // Change the second parameter from String to List<StopDTO>
    ManifestResponseDTO updateManifestManualOverride(Long manifestId, List<StopDTO> stops);;
}
