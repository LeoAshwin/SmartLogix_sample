package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.model.DeliveryException;

import java.util.List;

public interface ExceptionService {

    /**
     * Records a failed delivery attempt and determines the next suggested action.
     */
    DeliveryException reportException(Long fulfillmentId, Long driverId,
                                      String reasonCode, String details);

    /**
     * Retrieves all open exceptions for the Dispatcher's queue.
     */
    List<DeliveryException> getOpenExceptions();

    /**
     * Escalates an exception if the retry count exceeds the deterministic threshold.
     */
    void escalateException(Long exceptionId);

    /**
     * Resolves an exception (e.g., after a successful reattempt).
     */
    DeliveryException resolveException(Long exceptionId, String resolutionNotes);
}