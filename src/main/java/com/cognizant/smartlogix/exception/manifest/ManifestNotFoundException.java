package com.cognizant.smartlogix.exception.manifest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception specifically thrown when a requested Manifest ID does not exist in the system.
 * Returns a 404 Not Found status to the client.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ManifestNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with a message pinpointing the missing manifest.
     * @param message Detailed error info, usually containing the specific Manifest ID.
     */
    public ManifestNotFoundException(String message) {
        super(message);
    }
}