package com.cognizant.smartlogix.exception.manifest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ManifestNotFoundException extends RuntimeException {
    public ManifestNotFoundException(String message) {
        super(message);
    }
}
