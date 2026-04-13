package com.cognizant.smartlogix.model.data;

/**
 * Represents the delivery service level selected by the merchant/customer.
 * Service levels are deterministic and influence SLA rules,
 * delivery windows, and pricing.
 *
 */
public enum ServiceLevel {

    STANDARD,
    EXPRESS,
    SAME_DAY,
    NEXT_DAY
}
