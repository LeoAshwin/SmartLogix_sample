package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Manager.FulfillmentRequest;
import com.cognizant.smartlogix.dto.Manager.FulfillmentResponse;
import com.cognizant.smartlogix.service.FulfillmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing fulfillments.
 *
 * Module 4.1 – Order Ingestion & Validation
 *
 * RBAC:
 * - Create: MERCHANT or CUSTOMER (submitting an order) or ADMIN
 * - Read: MERCHANT, CUSTOMER (own order), DISPATCHER, LOGISTICS_MANAGER, ADMIN
 */
@RestController
@RequestMapping("/api/fulfillments")
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;

    public FulfillmentController(FulfillmentService fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    /**
     * Create a new fulfillment (Order Ingestion).
     * Merchants and customers submit orders; admin can create on behalf of any actor.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MERCHANT','CUSTOMER','ADMIN')")
    public FulfillmentResponse createFulfillment(
            @RequestBody FulfillmentRequest request) {

        return fulfillmentService.createFulfillment(request);
    }

    /**
     * Get fulfillment by ID.
     * Operational staff, the originating merchant/customer, and admin can view.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MERCHANT','CUSTOMER','DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public FulfillmentResponse getFulfillmentById(
            @PathVariable String id) {

        return fulfillmentService.getFulfillmentById(id);
    }
}