package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.manifest.RouteLegDTO;
import com.cognizant.smartlogix.model.RouteLeg;

import java.util.List;

public interface RouteLegService {
    /**
     * Deterministically generates legs for a manifest.
     * RouteLeg save(RouteLeg leg);
     */
    void generateLegsForManifest(Long manifestId, String stopsJson);

    /**
     * Fetches all legs for a specific manifest.
     */
    RouteLeg save(RouteLeg leg);
    List<RouteLegDTO> getLegsByManifestId(Long manifestId);
    void deleteByManifestId(Long manifestId);

}
