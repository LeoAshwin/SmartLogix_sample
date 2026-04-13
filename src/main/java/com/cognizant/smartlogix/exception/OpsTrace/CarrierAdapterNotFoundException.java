package com.cognizant.smartlogix.exception.OpsTrace;

public class CarrierAdapterNotFoundException
        extends RuntimeException {

    public CarrierAdapterNotFoundException(Long id) {
        super("Carrier adapter not found with id: " + id);
    }
}