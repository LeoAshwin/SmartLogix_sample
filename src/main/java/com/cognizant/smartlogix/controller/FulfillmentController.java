package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateFulfillmentRequest;
import com.cognizant.smartlogix.dto.request.UpdateFulfillmentStatusRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.FulfillmentResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import com.cognizant.smartlogix.service.FulfillmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/fulfillments")
@Tag(name = "Fulfillments", description = "Order ingestion, validation and lifecycle management")
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;

    public FulfillmentController(FulfillmentService fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    @Operation(summary = "Ingest a fulfillment (idempotent)")
    @PostMapping
    public ResponseEntity<ApiResponse<FulfillmentResponse>> ingest(
            @Valid @RequestBody CreateFulfillmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Fulfillment ingested", fulfillmentService.ingest(request)));
    }

    @Operation(summary = "Get fulfillment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FulfillmentResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(fulfillmentService.findById(id)));
    }

    @Operation(summary = "Get fulfillment by order ID")
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<ApiResponse<FulfillmentResponse>> getByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.ok(fulfillmentService.findByOrderId(orderId)));
    }

    @Operation(summary = "List all fulfillments (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<FulfillmentResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) FulfillmentStatus status) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<FulfillmentResponse> result = status != null
                ? PagedResponse.of(fulfillmentService.findByStatus(status, pr))
                : PagedResponse.of(fulfillmentService.findAll(pr));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @Operation(summary = "List fulfillments by merchant")
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<ApiResponse<PagedResponse<FulfillmentResponse>>> listByMerchant(
            @PathVariable UUID merchantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(fulfillmentService.findByMerchant(merchantId,
                        PageRequest.of(page, size, Sort.by("createdAt").descending())))));
    }

    @Operation(summary = "Update fulfillment status (state-machine enforced)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FulfillmentResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFulfillmentStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", fulfillmentService.updateStatus(id, request)));
    }

    @Operation(summary = "Delete PENDING fulfillment")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        fulfillmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Fulfillment deleted", null));
    }
}

