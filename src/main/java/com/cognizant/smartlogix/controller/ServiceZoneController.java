package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.Manager.ServiceZoneRequest;
import com.cognizant.smartlogix.dto.Manager.ServiceZoneResponse;
import com.cognizant.smartlogix.service.ServiceZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing service zones.
 *
 * Provides APIs to configure delivery zones along with
 * SLA rules and capacity settings.
 *
 * RBAC: Zone configuration is an infrastructure concern —
 * only Logistics Manager and Admin may create zones.
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
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public ServiceZoneResponse createServiceZone(
            @RequestBody ServiceZoneRequest request) {

        return serviceZoneService.createServiceZone(request);
    }
}
