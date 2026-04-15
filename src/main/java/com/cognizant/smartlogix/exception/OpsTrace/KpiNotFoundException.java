package com.cognizant.smartlogix.exception.OpsTrace;

import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;

public class KpiNotFoundException extends ResourceNotFoundException {

    public KpiNotFoundException(Long id) {
        super("KPI not found with id: " + id);
    }
}