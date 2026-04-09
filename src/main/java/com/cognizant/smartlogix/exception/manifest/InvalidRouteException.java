package com.cognizant.smartlogix.exception.manifest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a manifest route fails logical validation.
 * Used for scenarios such as invalid stop sequences or geographically unreachable destinations.
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class InvalidRouteException extends RuntimeException {

    /**
     * Constructs the exception with a specific validation failure message.
     * @param message Detailed explanation of the routing violation.
     */
    public InvalidRouteException(String message) {
        super(message);
    }
}