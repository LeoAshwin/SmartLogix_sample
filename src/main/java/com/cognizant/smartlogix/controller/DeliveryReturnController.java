package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.InitiateReturnRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.DeliveryReturnResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.enums.ReturnStatus;
import com.cognizant.smartlogix.service.DeliveryReturnService;
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
@RequestMapping("/returns")
@Tag(name = "Returns", description = "Reverse logistics and return lifecycle management")
public class DeliveryReturnController {

    private final DeliveryReturnService returnService;

    public DeliveryReturnController(DeliveryReturnService returnService) {
        this.returnService = returnService;
    }

    @Operation(summary = "Initiate a return")
    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryReturnResponse>> initiate(
            @Valid @RequestBody InitiateReturnRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Return initiated", returnService.initiate(request)));
    }

    @Operation(summary = "Get return by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryReturnResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(returnService.findById(id)));
    }

    @Operation(summary = "List returns by status")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<DeliveryReturnResponse>>> list(
            @RequestParam(required = false) ReturnStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(returnService.findByStatus(status != null ? status : ReturnStatus.INITIATED, pr))));
    }

    @Operation(summary = "Update return status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DeliveryReturnResponse>> updateStatus(
            @PathVariable UUID id, @RequestParam ReturnStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Return status updated", returnService.updateStatus(id, status)));
    }

    @Operation(summary = "Receive and inspect return")
    @PostMapping("/{id}/inspect")
    public ResponseEntity<ApiResponse<DeliveryReturnResponse>> inspect(
            @PathVariable UUID id, @RequestBody String inspectionResultJson) {
        return ResponseEntity.ok(ApiResponse.ok("Return inspected",
                returnService.receiveAndInspect(id, inspectionResultJson)));
    }
}

