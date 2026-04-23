package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Driver.request.ExceptionReportRequest;
import com.cognizant.smartlogix.dto.Driver.response.ExceptionResponse;
import com.cognizant.smartlogix.dto.ResponseMapper;
import com.cognizant.smartlogix.model.DeliveryException;
import com.cognizant.smartlogix.service.ExceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver/exceptions")
@RequiredArgsConstructor
public class ExceptionController {

    private final ExceptionService exceptionService;
    private final ResponseMapper mapper;

    /**
     * Driver reports a delivery exception (e.g. no-access, address issue).
     * Only DRIVER may report an exception in the field.
     */
    @PostMapping("/report")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ExceptionResponse> reportException(
            @Valid @RequestBody ExceptionReportRequest request) {

        DeliveryException ex = exceptionService.reportException(
                request.fulfillmentId(),
                request.driverId(),
                request.reasonCode(),
                request.details()
        );

        return ResponseEntity.ok(mapper.toExceptionResponse(ex));
    }

    /**
     * View all open (unresolved) exceptions.
     * Dispatcher and Logistics Manager handle exception queues.
     */
    @GetMapping("/open")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<ExceptionResponse>> getOpenExceptions() {
        List<DeliveryException> openEx = exceptionService.getOpenExceptions();
        return ResponseEntity.ok(mapper.toExceptionResponseList(openEx));
    }

    /**
     * Dispatcher or manager resolves an exception with notes.
     */
    @PatchMapping("/{exceptionId}/resolve")
    @PreAuthorize("hasAnyRole('DISPATCHER','LOGISTICS_MANAGER')")
    public ResponseEntity<ExceptionResponse> resolveException(
            @PathVariable Long exceptionId,
            @RequestParam String notes) {

        DeliveryException resolvedEx = exceptionService.resolveException(exceptionId, notes);
        return ResponseEntity.ok(mapper.toExceptionResponse(resolvedEx));
    }

    /**
     * Reference list of valid reason codes.
     * Driver, dispatcher, and manager all need this lookup.
     */
    @GetMapping("/reasons")
    @PreAuthorize("hasAnyRole('DRIVER','DISPATCHER','LOGISTICS_MANAGER')")
    public ResponseEntity<List<String>> getValidReasonCodes() {
        return ResponseEntity.ok(exceptionService.getAvailableReasonCodes());
    }

    /**
     * Escalated exceptions require management visibility.
     */
    @GetMapping("/escalated")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public ResponseEntity<List<ExceptionResponse>> getEscalatedExceptions() {
        List<DeliveryException> escalated = exceptionService.getExceptionsByStatus("ESCALATED");
        return ResponseEntity.ok(mapper.toExceptionResponseList(escalated));
    }
}