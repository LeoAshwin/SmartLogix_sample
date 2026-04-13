package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.CarrierAdapterRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.CarrierAdapterResponseDTO;
import com.cognizant.smartlogix.model.data.CarrierAdapter;
import com.cognizant.smartlogix.service.CarrierAdapterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carrier-adapters")
public class CarrierAdapterController {

    private final CarrierAdapterService service;

    public CarrierAdapterController(CarrierAdapterService service) {
        this.service = service;
    }

    // ✅ Create adapter
    @PostMapping
    public CarrierAdapterResponseDTO create(
            @RequestBody CarrierAdapterRequestDTO dto) {

        CarrierAdapter adapter = new CarrierAdapter();
        adapter.setCarrierId(dto.carrierId());
        adapter.setProtocol(dto.protocol());
        adapter.setCredentialsJson(dto.credentialsJson());
        adapter.setSandboxEnabled(dto.sandboxEnabled());
        adapter.setStatus(dto.status());

        CarrierAdapter saved = service.createAdapter(adapter);

        return new CarrierAdapterResponseDTO(
                saved.getAdapterId(),
                saved.getCarrierId(),
                saved.getProtocol(),
                saved.getSandboxEnabled(),
                saved.getStatus(),
                saved.getLastSyncAt()
        );
    }

    // ✅ Get all adapters
    @GetMapping
    public List<CarrierAdapterResponseDTO> getAll() {
        return service.getAllAdapters()
                .stream()
                .map(a -> new CarrierAdapterResponseDTO(
                        a.getAdapterId(),
                        a.getCarrierId(),
                        a.getProtocol(),
                        a.getSandboxEnabled(),
                        a.getStatus(),
                        a.getLastSyncAt()))
                .toList();
    }

    // ✅ Dummy sync (Sandbox + Webhook)
    @PostMapping("/{id}/sync")
    public Map<String, Object> sync(@PathVariable Long id) {
        return service.syncAdapter(id);
    }
}