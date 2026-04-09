package com.cognizant.smartlogix.exception.manifest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global exception thrown when a requested resource cannot be found in the database.
 * Triggers a 404 Not Found response to the client.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message identifying the missing entity.
     * @param message Detailed error information, typically including the entity type and ID.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}