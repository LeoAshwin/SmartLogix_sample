package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.FleetSec.DriverRequest;
import com.cognizant.smartlogix.model.Driver;
import com.cognizant.smartlogix.service.DriverService;
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

    @PostMapping
    public Driver create(@RequestBody DriverRequest request) {
        return driverService.create(request);
    }

    @GetMapping
    public List<Driver> getAll() {
        return driverService.getAll();
    }

    @GetMapping("/{id}")
    public Driver getById(@PathVariable UUID id) {
        return driverService.getById(id);
    }
}
