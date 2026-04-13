package com.cognizant.smartlogix.exception.OpsTrace;

public class AuditPackageNotFoundException
        extends RuntimeException {

    public AuditPackageNotFoundException(Long id) {
        super("Audit package not found with id: " + id);
    }
}