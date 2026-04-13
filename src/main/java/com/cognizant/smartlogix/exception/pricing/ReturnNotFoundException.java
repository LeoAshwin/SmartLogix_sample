package com.cognizant.smartlogix.exception.pricing;

/**
 * Thrown when a Return is not found in the system.
 */
public class ReturnNotFoundException extends RuntimeException {

    public ReturnNotFoundException(String message) {
        super(message);
    }
}