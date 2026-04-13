package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.Manager.FulfillmentRequest;
import com.cognizant.smartlogix.dto.Driver.response.FulfillmentResponse;

/**
 * Service interface for order ingestion and fulfillment creation.
 *
 * Module 4.1 – Order Ingestion & Validation
 */

public interface FulfillmentService {

    /**
     * Validates and creates a new fulfillment.
     */
    FulfillmentResponse createFulfillment(FulfillmentRequest request);
    FulfillmentResponse getFulfillmentById(String id);

}

