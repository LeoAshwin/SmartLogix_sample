package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Manager.ServiceZoneRequest;
import com.cognizant.smartlogix.dto.Manager.ServiceZoneResponse;
import com.cognizant.smartlogix.service.ServiceZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing service zones.
 *
 * Provides APIs to configure delivery zones along with
 * SLA rules and capacity settings.
 */
@RestController
@RequestMapping("/api/service-zones")
public class ServiceZoneController {

    private final ServiceZoneService serviceZoneService;

    public ServiceZoneController(ServiceZoneService serviceZoneService) {
        this.serviceZoneService = serviceZoneService;
    }

    /**
     * Creates a new service zone.
     *
     * Accepts zone configuration details and
     * persists them through the service layer.
     *
     * @param request service zone creation request
     * @return created service zone details
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceZoneResponse createServiceZone(
            @RequestBody ServiceZoneRequest request) {

        return serviceZoneService.createServiceZone(request);
    }
}
