package com.cognizant.smartlogix.exception.manifest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the total weight of assigned orders exceeds the vehicle's maximum capacity.
 * Automatically maps to a 400 Bad Request response in the REST API.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CapacityExceededException extends RuntimeException {

    /**
     * Constructs a new exception with a specific error message detail.
     * @param message The detailed reason for the capacity failure.
     */
    public CapacityExceededException(String message) {
        super(message);
    }
}