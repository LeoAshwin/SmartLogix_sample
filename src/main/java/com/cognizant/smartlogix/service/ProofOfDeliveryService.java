package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.request.CaptureProofOfDeliveryRequest;
import com.cognizant.smartlogix.dto.response.ProofOfDeliveryResponse;
import com.cognizant.smartlogix.model.enums.PODStatus;

import java.util.UUID;

public interface ProofOfDeliveryService {
    ProofOfDeliveryResponse capture(CaptureProofOfDeliveryRequest request);
    ProofOfDeliveryResponse findById(UUID id);
    ProofOfDeliveryResponse findByFulfillment(UUID fulfillmentId);
    ProofOfDeliveryResponse updateStatus(UUID id, PODStatus status);
}

