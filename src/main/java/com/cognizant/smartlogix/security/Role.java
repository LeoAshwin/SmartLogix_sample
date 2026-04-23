package com.cognizant.smartlogix.security;

/**
 * RBAC roles for all SmartLogix actors.
 * Stored as VARCHAR in the database via @Enumerated(EnumType.STRING).
 *
 * Spring Security uses the "ROLE_" prefix internally when checking
 * hasRole("DRIVER") — it maps to the granted authority "ROLE_DRIVER".
 */
public enum Role {
    /** Configures zones, SLAs, carrier contracts, monitors operations. */
    LOGISTICS_MANAGER,

    /** Creates manifests, assigns drivers/vehicles, manages exceptions. */
    DISPATCHER,

    /** Receives assignments, updates status, captures POD, records exceptions. */
    DRIVER,

    /** Places orders, tracks delivery, confirms receipt, initiates returns. */
    CUSTOMER,

    /** Submits orders, views fulfillment status, reconciles settlements. */
    MERCHANT,

    /** Receives manifests, updates delivery events, submits invoices. */
    CARRIER,

    /** Reconciles carrier invoices, calculates payouts and commissions. */
    FINANCE_OFFICER,

    /** Configures service areas, pricing rules, user roles, integrations. */
    ADMIN
}
