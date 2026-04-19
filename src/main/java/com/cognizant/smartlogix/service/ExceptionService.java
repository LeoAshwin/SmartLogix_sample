package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.model.DeliveryException;

import java.util.List;

public interface ExceptionService {

    DeliveryException reportException(String fulfillmentId, String driverId,
                                      String reasonCode, String details);

    List<DeliveryException> getOpenExceptions();

    void escalateException(Long exceptionId);

    DeliveryException resolveException(Long exceptionId, String resolutionNotes);

    List<String> getAvailableReasonCodes();

    List<DeliveryException> getExceptionsByStatus(String status);
}