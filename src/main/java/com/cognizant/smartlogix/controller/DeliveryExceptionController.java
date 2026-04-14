package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.RaiseDeliveryExceptionRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.DeliveryExceptionResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.enums.ExceptionStatus;
import com.cognizant.smartlogix.service.DeliveryExceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/exceptions")
@Tag(name = "Exceptions", description = "Delivery exception management and escalation")
public class DeliveryExceptionController {

    private final DeliveryExceptionService exceptionService;

    public DeliveryExceptionController(DeliveryExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @Operation(summary = "Raise a delivery exception")
    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryExceptionResponse>> raise(
            @Valid @RequestBody RaiseDeliveryExceptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Exception raised", exceptionService.raise(request)));
    }

    @Operation(summary = "Get exception by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryExceptionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(exceptionService.findById(id)));
    }

    @Operation(summary = "Get exceptions for a fulfillment")
    @GetMapping("/fulfillment/{fulfillmentId}")
    public ResponseEntity<ApiResponse<List<DeliveryExceptionResponse>>> getByFulfillment(
            @PathVariable UUID fulfillmentId) {
        return ResponseEntity.ok(ApiResponse.ok(exceptionService.findByFulfillment(fulfillmentId)));
    }

    @Operation(summary = "List exceptions by status (dispatcher queue)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<DeliveryExceptionResponse>>> list(
            @RequestParam(required = false) ExceptionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("raisedAt").descending());
        PagedResponse<DeliveryExceptionResponse> result = status != null
                ? PagedResponse.of(exceptionService.findByStatus(status, pr))
                : PagedResponse.of(exceptionService.findByStatus(ExceptionStatus.OPEN, pr));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Operation(summary = "Count open exceptions (dispatcher dashboard metric)")
    @GetMapping("/count/open")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countOpen() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("openExceptions", exceptionService.countOpen())));
    }

    @Operation(summary = "Resolve exception")
    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<DeliveryExceptionResponse>> resolve(
            @PathVariable UUID id, @RequestParam String resolution) {
        return ResponseEntity.ok(ApiResponse.ok("Exception resolved", exceptionService.resolve(id, resolution)));
    }

    @Operation(summary = "Escalate exception")
    @PostMapping("/{id}/escalate")
    public ResponseEntity<ApiResponse<DeliveryExceptionResponse>> escalate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Exception escalated", exceptionService.escalate(id)));
    }
}

