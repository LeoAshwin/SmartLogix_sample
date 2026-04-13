package com.cognizant.smartlogix.exception.OpsTrace;

public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(Long id) {
        super("Report not found with id: " + id);
    }
}
