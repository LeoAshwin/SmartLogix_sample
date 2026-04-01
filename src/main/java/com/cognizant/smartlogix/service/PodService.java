package com.cognizant.smartlogix.service;


import com.cognizant.smartlogix.dto.Driver.PodSubmissionRequest;
import com.cognizant.smartlogix.model.Pod;

import java.util.Optional;

public interface PodService {

    /**
     * Processes a POD submission from the Driver App.
     * Includes SHA-256 checksum generation for tamper evidence.
     */
    Pod submitPod(PodSubmissionRequest request);

    /**
     * Retrieves POD details for a specific fulfillment.
     */
    Optional<Pod> getPodByFulfillment(Long fulfillmentId);

    /**
     * Verifies the integrity of a POD using the stored SHA-256 hash.
     */
    boolean verifyPodIntegrity(Long podId);
}