package com.cognizant.smartlogix.model.data;

/**
 * Defines the valid lifecycle states for a Manifest.
 * Used to control transitions and enforce business rules during the delivery process.
 */
public enum ManifestStatus {
    /** Initial state when the manifest is created but not yet finalized. */
    PENDING,

    /** Manifest has been reviewed and linked to a specific driver and vehicle. */
    ASSIGNED,

    /** The trip has commenced and the vehicle is currently on the route. */
    IN_TRANSIT,

    /** All stops have been successfully completed and documented. */
    DELIVERED,

    /** The manifest has been voided and is no longer active for execution. */
    CANCELLED
}