package com.cognizant.smartlogix.exception.OpsTrace;

public class KpiNotFoundException extends RuntimeException {

    public KpiNotFoundException(Long id) {
        super("KPI not found with id: " + id);
    }
}