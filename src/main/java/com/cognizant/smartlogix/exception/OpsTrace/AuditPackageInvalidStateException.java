


package com.cognizant.smartlogix.exception.OpsTrace;

/**
 * Thrown when an AuditPackage is in an invalid state
 * for the requested operation.
 *
 * Example cases:
 * - Trying to modify a finalized audit package
 * - Trying to regenerate a locked audit package
 */
public class AuditPackageInvalidStateException extends RuntimeException {

    public AuditPackageInvalidStateException(String message) {
        super(message);
    }

    public AuditPackageInvalidStateException(Long packageId, String state) {
        super("Audit package with id " + packageId +
                " is in invalid state: " + state);
    }
}