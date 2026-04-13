package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Manager.FulfillmentRequest;
import com.cognizant.smartlogix.dto.Driver.response.FulfillmentResponse;
import com.cognizant.smartlogix.service.FulfillmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing fulfillments.
 *
 * Module 4.1 – Order Ingestion & Validation
 */
@RestController
@RequestMapping("/api/fulfillments")
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;

    public FulfillmentController(FulfillmentService fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    /**
     * Create a new fulfillment (Order Ingestion)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FulfillmentResponse createFulfillment(
            @RequestBody FulfillmentRequest request) {

        return fulfillmentService.createFulfillment(request);
    }

    /**
     * Get fulfillment by ID
     */
    @GetMapping("/{id}")
    public FulfillmentResponse getFulfillmentById(
            @PathVariable String id) {

        return fulfillmentService.getFulfillmentById(id);
    }
}