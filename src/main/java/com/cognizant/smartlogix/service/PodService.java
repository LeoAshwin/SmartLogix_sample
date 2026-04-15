package com.cognizant.smartlogix.service;


import com.cognizant.smartlogix.dto.Driver.request.PodSubmissionRequest;
import com.cognizant.smartlogix.model.Pod;

import java.util.Optional;

public interface PodService {

    Pod submitPod(PodSubmissionRequest request);

    Optional<Pod> getPodByFulfillment(Long fulfillmentId);

    boolean verifyPodIntegrity(Long podId);
}