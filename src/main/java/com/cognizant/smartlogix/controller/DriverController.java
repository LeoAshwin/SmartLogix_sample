package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.FleetSec.DriverRequest;
import com.cognizant.smartlogix.model.Driver;
import com.cognizant.smartlogix.service.DriverService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    /** Admin or logistics manager onboards a new driver. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER')")
    public Driver create(@RequestBody DriverRequest request) {
        return driverService.create(request);
    }

    /** Operational staff and the driver themselves see the fleet roster. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER','DRIVER')")
    public List<Driver> getAll() {
        return driverService.getAll();
    }

    /** Individual driver profile. */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER','DRIVER')")
    public Driver getById(@PathVariable UUID id) {
        return driverService.getById(id);
    }
}
