package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.FleetSec.CarrierRequest;
import com.cognizant.smartlogix.model.Carrier;
import com.cognizant.smartlogix.service.CarrierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/carriers")
public class CarrierController {

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @PostMapping
    public Carrier create(@RequestBody CarrierRequest request) {
        return carrierService.create(request);
    }

    @GetMapping
    public List<Carrier> getAll() {
        return carrierService.getAll();
    }

    @GetMapping("/{id}")
    public Carrier getById(@PathVariable UUID id) {
        return carrierService.getById(id);
    }
}
