package com.cognizant.smartlogix.exception;


import com.cognizant.smartlogix.dto.Driver.response.ErrorResponse;
import com.cognizant.smartlogix.exception.driver.IntegrityCheckException;
import com.cognizant.smartlogix.exception.driver.InvalidStateTransitionException;
import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;
import com.cognizant.smartlogix.exception.manifest.CapacityExceededException;
import com.cognizant.smartlogix.exception.manifest.InvalidRouteException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    // 1. Handle the "Read-Only Lock" and "Invalid Sequence" errors
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Execution Logic Error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 2. Handle "Manifest Not Found" errors
    @ExceptionHandler(com.cognizant.smartlogix.exception.manifest.EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // 3. NEW: Handle Logistics Business Rules (Capacity & Route issues)
    @ExceptionHandler({CapacityExceededException.class, InvalidRouteException.class})
    public ResponseEntity<Object> handleLogisticsBusinessErrors(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value()); // 422 is great for logic errors
        body.put("error", "Logistics Validation Failed");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}