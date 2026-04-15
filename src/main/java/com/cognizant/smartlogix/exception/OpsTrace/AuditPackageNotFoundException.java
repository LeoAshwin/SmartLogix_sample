package com.cognizant.smartlogix.exception.OpsTrace;

import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;

public class AuditPackageNotFoundException extends ResourceNotFoundException {

    public AuditPackageNotFoundException(Long id) {
        super("Audit Package not found with id: " + id);
    }
}