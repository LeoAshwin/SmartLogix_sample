package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.pricing.ReturnCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.ReturnResponse;
import com.cognizant.smartlogix.service.ReturnService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    /**
     * Customer or merchant initiates a return request.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','MERCHANT')")
    public ResponseEntity<ReturnResponse> createReturn(
            @RequestBody ReturnCreateRequest request) {
        return ResponseEntity.ok(returnService.createReturn(request));
    }

    /**
     * View a return by its ID.
     * All relevant stakeholders can see return status.
     */
    @GetMapping("/{returnId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','MERCHANT','DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<ReturnResponse> getReturnById(
            @PathVariable Long returnId) {
        return ResponseEntity.ok(returnService.getReturnById(returnId));
    }

    /**
     * View all returns for a fulfillment.
     */
    @GetMapping("/fulfillment/{fulfillmentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','MERCHANT','DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<ReturnResponse>> getReturnsByFulfillment(
            @PathVariable String fulfillmentId) {
        return ResponseEntity.ok(
                returnService.getReturnsByFulfillmentId(fulfillmentId));
    }
}
