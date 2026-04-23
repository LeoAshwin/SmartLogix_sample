package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.CarrierAdapterRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.CarrierAdapterResponseDTO;
import com.cognizant.smartlogix.service.CarrierAdapterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrier-adapters")
public class CarrierAdapterController {

    private final CarrierAdapterService service;

    public CarrierAdapterController(CarrierAdapterService service) {
        this.service = service;
    }

    /** Admin configures a new carrier adapter integration. */
    @PostMapping
    @PreAuthorize("hasAnyRole('CARRIER','ADMIN')")
    public CarrierAdapterResponseDTO create(
            @RequestBody CarrierAdapterRequestDTO dto) {
        return service.create(dto);
    }

    /** Carrier and admin browse adapters. */
    @GetMapping
    @PreAuthorize("hasAnyRole('CARRIER','ADMIN')")
    public List<CarrierAdapterResponseDTO> getAll() {
        return service.getAll();
    }

    /** Single adapter lookup. */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CARRIER','ADMIN')")
    public CarrierAdapterResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /** Full update of adapter configuration. */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CARRIER','ADMIN')")
    public CarrierAdapterResponseDTO update(
            @PathVariable Long id,
            @RequestBody CarrierAdapterRequestDTO dto) {
        return service.update(id, dto);
    }

    /** Partial update of adapter configuration. */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('CARRIER','ADMIN')")
    public CarrierAdapterResponseDTO patch(
            @PathVariable Long id,
            @RequestBody CarrierAdapterRequestDTO dto) {
        return service.patch(id, dto);
    }

    /**
     * Carrier sync endpoint — triggers a manual sync cycle.
     */
    @PostMapping("/{id}/sync")
    @PreAuthorize("hasAnyRole('CARRIER','ADMIN')")
    public CarrierAdapterResponseDTO sync(@PathVariable Long id) {
        return service.sync(id);
    }

    /** Delete an adapter. Admin-only destructive operation. */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Webhook endpoint for carrier push events.
     * Open to CARRIER and ADMIN — typically called by the carrier's system.
     */
    @PostMapping("/webhook/events")
    @PreAuthorize("hasAnyRole('CARRIER','ADMIN')")
    public void receiveWebhook(@RequestBody String payload) {
        // Event processing handled elsewhere
    }
}