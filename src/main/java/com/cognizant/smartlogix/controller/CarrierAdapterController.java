package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.CarrierAdapterRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.CarrierAdapterResponseDTO;
import com.cognizant.smartlogix.service.CarrierAdapterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrier-adapters")
public class CarrierAdapterController {

    private final CarrierAdapterService service;

    public CarrierAdapterController(CarrierAdapterService service) {
        this.service = service;
    }

    @PostMapping
    public CarrierAdapterResponseDTO create(
            @RequestBody CarrierAdapterRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<CarrierAdapterResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CarrierAdapterResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public CarrierAdapterResponseDTO update(
            @PathVariable Long id,
            @RequestBody CarrierAdapterRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    public CarrierAdapterResponseDTO patch(
            @PathVariable Long id,
            @RequestBody CarrierAdapterRequestDTO dto) {
        return service.patch(id, dto);
    }

    /**
     * Carrier sync endpoint (PDF feature)
     */
    @PostMapping("/{id}/sync")
    public CarrierAdapterResponseDTO sync(@PathVariable Long id) {
        return service.sync(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /**
     * ✅ Webhook endpoint (PDF feature)
     * No DB changes required.
     */
    @PostMapping("/webhook/events")
    public void receiveWebhook(@RequestBody String payload) {
        // Event processing handled elsewhere
    }
}