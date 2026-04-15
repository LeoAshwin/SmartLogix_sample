package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Driver.request.ExceptionReportRequest;
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

    @PostMapping("/report")
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


    @GetMapping("/open")
    public ResponseEntity<List<ExceptionResponse>> getOpenExceptions() {
        List<DeliveryException> openEx = exceptionService.getOpenExceptions();
        return ResponseEntity.ok(mapper.toExceptionResponseList(openEx));
    }

    @PatchMapping("/{exceptionId}/resolve")
    public ResponseEntity<ExceptionResponse> resolveException(
            @PathVariable Long exceptionId,
            @RequestParam String notes) {

        DeliveryException resolvedEx = exceptionService.resolveException(exceptionId, notes);
        return ResponseEntity.ok(mapper.toExceptionResponse(resolvedEx));
    }

    @GetMapping("/reasons")
    public ResponseEntity<List<String>> getValidReasonCodes() {
        return ResponseEntity.ok(exceptionService.getAvailableReasonCodes());
    }

    @GetMapping("/escalated")
    public ResponseEntity<List<ExceptionResponse>> getEscalatedExceptions() {
        List<DeliveryException> escalated = exceptionService.getExceptionsByStatus("ESCALATED");
        return ResponseEntity.ok(mapper.toExceptionResponseList(escalated));
    }
}