
package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.Manager.ServiceZoneRequest;
import com.cognizant.smartlogix.dto.Driver.response.ServiceZoneResponse;

/**
 * Service interface for service zone management.
 */
public interface ServiceZoneService {

    /**
     * Creates a service zone configuration.
     *
     * @param request service zone request
     * @return created service zone response
     */
    ServiceZoneResponse createServiceZone(ServiceZoneRequest request);
}
