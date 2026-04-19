package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.manifest.ManifestRequestDTO;
import com.cognizant.smartlogix.dto.manifest.ManifestResponseDTO;
import com.cognizant.smartlogix.dto.manifest.StopDTO;

import java.time.LocalDate;
import java.util.List;

public interface ManifestService {

    ManifestResponseDTO generateDeterministicManifest(ManifestRequestDTO request);

    ManifestResponseDTO getManifestById(Long manifestId);

    ManifestResponseDTO dispatchManifest(Long manifestId);

    void deleteManifest(Long manifestId);

    ManifestResponseDTO startTrip(Long manifestId);

    ManifestResponseDTO markStopAsCompleted(Long manifestId, Long fulfillmentId);

    List<ManifestResponseDTO> searchManifests(String status, Long driverId, LocalDate date);

    void cancelManifest(Long manifestId);

    byte[] exportManifestToPdf(Long id);

    ManifestResponseDTO updateManifestManualOverride(Long manifestId, List<StopDTO> stops);

    List<ManifestResponseDTO> getManifestsByStatus(String status);

    List<ManifestResponseDTO> getManifestsByVehicle(Long vehicleId);
}