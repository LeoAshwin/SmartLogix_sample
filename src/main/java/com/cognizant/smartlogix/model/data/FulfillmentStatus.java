package com.cognizant.smartlogix.model.data;

/**
 * Represents the lifecycle status of a fulfillment order.
 * This enum is used to track the progress of an order from
 * creation to completion or failure.
 *
 */
public enum FulfillmentStatus {

    PENDING,
    ASSIGNED,
    EN_ROUTE,
    DELIVERED,
    FAILED,
    RETURNED
}