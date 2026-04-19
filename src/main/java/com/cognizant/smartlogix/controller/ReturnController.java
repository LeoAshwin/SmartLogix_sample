package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.pricing.ReturnCreateRequest;
import com.cognizant.smartlogix.dto.pricing.response.ReturnResponse;
import com.cognizant.smartlogix.service.ReturnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping
    public ResponseEntity<ReturnResponse> createReturn(
            @RequestBody ReturnCreateRequest request) {
        return ResponseEntity.ok(returnService.createReturn(request));
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnResponse> getReturnById(
            @PathVariable Long returnId) {
        return ResponseEntity.ok(returnService.getReturnById(returnId));
    }

    @GetMapping("/fulfillment/{fulfillmentId}")
    public ResponseEntity<List<ReturnResponse>> getReturnsByFulfillment(
            @PathVariable String fulfillmentId) {
        return ResponseEntity.ok(
                returnService.getReturnsByFulfillmentId(fulfillmentId));
    }
}
