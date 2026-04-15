package com.cognizant.smartlogix.exception.OpsTrace;

import com.cognizant.smartlogix.exception.driver.ResourceNotFoundException;

public class CarrierAdapterNotFoundException extends ResourceNotFoundException {

    public CarrierAdapterNotFoundException(Long id) {
        super("Carrier Adapter not found with id: " + id);
    }
}