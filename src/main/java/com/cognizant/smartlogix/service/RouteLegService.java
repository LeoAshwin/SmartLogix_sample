package com.cognizant.smartlogix.service;


public interface RouteLegService {

    void generateLegsForManifest(Long manifestId, String stopsJson);

    void deleteByManifestId(Long manifestId);
}