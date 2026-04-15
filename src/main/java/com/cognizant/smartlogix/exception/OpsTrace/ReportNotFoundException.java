package com.cognizant.smartlogix.exception.OpsTrace;

import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;

public class ReportNotFoundException extends ResourceNotFoundException {

    public ReportNotFoundException(Long id) {
        super("Report not found with id: " + id);
    }
}