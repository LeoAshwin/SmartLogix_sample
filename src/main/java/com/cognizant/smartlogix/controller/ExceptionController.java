package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Driver.ExceptionReportRequest;
import com.cognizant.smartlogix.dto.Driver.response.ExceptionResponse;
import com.cognizant.smartlogix.dto.ResponseMapper;
import com.cognizant.smartlogix.model.DeliveryException;
import com.cognizant.smartlogix.service.ExceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver/exceptions")
@RequiredArgsConstructor
public class ExceptionController {

    private final ExceptionService exceptionService;
    private final ResponseMapper mapper;

    /**
     * Requirement 4.6: Report a delivery failure
     */
    @PostMapping("/report")
    public ResponseEntity<ExceptionResponse> reportException(
            @Valid @RequestBody ExceptionReportRequest request) {

        DeliveryException ex = exceptionService.reportException(
                request.getFulfillmentId(),
                request.getDriverId(),
                request.getReasonCode(),
                request.getDetails()
        );

        return ResponseEntity.ok(mapper.toExceptionResponse(ex));
    }

    // For the Dispatcher Dashboard: View all open exceptions
    @GetMapping("/open")
    public ResponseEntity<List<ExceptionResponse>> getOpenExceptions() {
        List<DeliveryException> openEx = exceptionService.getOpenExceptions();
        return ResponseEntity.ok(mapper.toExceptionResponseList(openEx));
    }

    // Resolve an exception (Requirement 4.6 logic)
    @PatchMapping("/{exceptionId}/resolve")
    public ResponseEntity<ExceptionResponse> resolveException(
            @PathVariable Long exceptionId,
            @RequestParam String notes) {

        DeliveryException resolvedEx = exceptionService.resolveException(exceptionId, notes);
        return ResponseEntity.ok(mapper.toExceptionResponse(resolvedEx));
    }
}