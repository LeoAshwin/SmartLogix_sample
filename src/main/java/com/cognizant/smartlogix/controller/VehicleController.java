package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.FleetSec.VehicleRequest;
import com.cognizant.smartlogix.model.Vehicle;
import com.cognizant.smartlogix.service.VehicleService;
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

    @PostMapping
    public Vehicle create(@RequestBody VehicleRequest request) {
        return vehicleService.create(request);
    }

    @GetMapping
    public List<Vehicle> getAll() {
        return vehicleService.getAll();
    }

    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable UUID id) {
        return vehicleService.getById(id);
    }
}
