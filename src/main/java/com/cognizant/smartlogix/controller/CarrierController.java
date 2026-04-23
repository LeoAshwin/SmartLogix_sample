package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.FleetSec.CarrierRequest;
import com.cognizant.smartlogix.model.Carrier;
import com.cognizant.smartlogix.service.CarrierService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /** Only admin or logistics manager can onboard a new carrier. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER')")
    public Carrier create(@RequestBody CarrierRequest request) {
        return carrierService.create(request);
    }

    /** Carrier itself, admin, and logistics manager can browse the carrier list. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER','CARRIER')")
    public List<Carrier> getAll() {
        return carrierService.getAll();
    }

    /** Same as getAll — individual lookup. */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER','CARRIER')")
    public Carrier getById(@PathVariable UUID id) {
        return carrierService.getById(id);
    }
}
