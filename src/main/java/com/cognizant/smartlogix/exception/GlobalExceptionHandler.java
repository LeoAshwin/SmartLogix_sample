package com.cognizant.smartlogix.exception;

import com.cognizant.smartlogix.exception.pricing.ReturnNotFoundException;
import com.cognizant.smartlogix.exception.pricing.PricingRuleNotFoundException;
import com.cognizant.smartlogix.exception.pricing.CarrierBookingNotFoundException;
import com.cognizant.smartlogix.exception.pricing.CarrierSettlementNotFoundException;


import com.cognizant.smartlogix.dto.Driver.response.ErrorResponse;
import com.cognizant.smartlogix.exception.driver.IntegrityCheckException;
import com.cognizant.smartlogix.exception.driver.InvalidStateTransitionException;
import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
//@RestControllerAdvice  uses Aspect Oriented Programming
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Helper method to keep your handler clean
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(response, status);
    }

    // 1. Handles State Conflicts (409 Conflict)
    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidStateTransitionException ex, HttpServletRequest request) {
        log.warn("Business Logic Violation: {}", ex.getMessage());

        return buildResponse(HttpStatus.CONFLICT, "State Error", ex.getMessage(), request);
    }

    // 2. Handles Missing Data (404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    // 422 Unprocessable Entity: For integrity/security failures
    @ExceptionHandler(IntegrityCheckException.class)
    public ResponseEntity<ErrorResponse> handleIntegrity(IntegrityCheckException ex, HttpServletRequest request) {
        log.error("SECURITY ALERT: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "DATA_INTEGRITY_FAILURE", ex.getMessage(), request);
    }

    // Handles 405 Method Not Allowed
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("HTTP Method Not Supported: {} at path {}", ex.getMethod(), request.getRequestURI());
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", ex.getMessage(), request);
    }

    // Handles 404 for Static Resources (like favicon.ico)
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Static resource not found: {} at path {}", ex.getResourcePath(), request.getRequestURI());
        return buildResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "The requested static resource was not found", request);
    }


    // 4. Catch-all for unexpected server errors (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        // Log the full stack trace for debugging
        log.error("Unexpected System Error at path: {}", request.getRequestURI(), ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please contact support.",
                request
        );
    }

        @ExceptionHandler(ReturnNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleReturnNotFound(
                ReturnNotFoundException ex,
                HttpServletRequest request) {

            return buildResponse(
                    HttpStatus.NOT_FOUND,
                    "RETURN_NOT_FOUND",
                    ex.getMessage(),
                    request
            );

    }
    @ExceptionHandler(PricingRuleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePricingRuleNotFound(
            PricingRuleNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "PRICING_RULE_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }
    @ExceptionHandler(CarrierBookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCarrierBookingNotFound(
            CarrierBookingNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "CARRIER_BOOKING_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }
    @ExceptionHandler(CarrierSettlementNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCarrierSettlementNotFound(
            CarrierSettlementNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "CARRIER_SETTLEMENT_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }
}