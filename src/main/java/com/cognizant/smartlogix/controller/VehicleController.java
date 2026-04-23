package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.FleetSec.VehicleRequest;
import com.cognizant.smartlogix.model.Vehicle;
import com.cognizant.smartlogix.service.VehicleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /** Admin or manager adds a vehicle to the fleet. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER')")
    public Vehicle create(@RequestBody VehicleRequest request) {
        return vehicleService.create(request);
    }

    /** Dispatcher needs to see available vehicles for manifest creation. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER','DISPATCHER')")
    public List<Vehicle> getAll() {
        return vehicleService.getAll();
    }

    /** Individual vehicle profile. */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER','DISPATCHER')")
    public Vehicle getById(@PathVariable UUID id) {
        return vehicleService.getById(id);
    }
}
