package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.exception.OpsTrace.CarrierAdapterNotFoundException;
import com.cognizant.smartlogix.model.data.CarrierAdapter;
import com.cognizant.smartlogix.repository.CarrierAdapterRepository;
import com.cognizant.smartlogix.service.CarrierAdapterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CarrierAdapterServiceImplementation
        implements CarrierAdapterService {

    private final CarrierAdapterRepository repository;

    public CarrierAdapterServiceImplementation(
            CarrierAdapterRepository repository) {
        this.repository = repository;
    }

    @Override
    public CarrierAdapter createAdapter(CarrierAdapter adapter) {
        return repository.save(adapter);
    }

    @Override
    public List<CarrierAdapter> getAllAdapters() {
        return repository.findAll();
    }

    @Override
    public CarrierAdapter getAdapterById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new CarrierAdapterNotFoundException(id));
    }

    // ✅ Dummy Sync + Webhook Simulation
    @Override
    public Map<String, Object> syncAdapter(Long adapterId) {

        CarrierAdapter adapter = getAdapterById(adapterId);

        // Dummy last sync
        String now = LocalDateTime.now().toString();
        adapter.setLastSyncAt(now);
        repository.save(adapter);

        // ✅ Sandbox + Webhook simulation (LOGGING ONLY)
        if (Boolean.TRUE.equals(adapter.getSandboxEnabled())) {
            System.out.println(
                    "[SANDBOX] Webhook simulated for carrier "
                            + adapter.getCarrierId()
            );
        } else {
            System.out.println(
                    "[PRODUCTION] Webhook simulated for carrier "
                            + adapter.getCarrierId()
            );
        }

        return Map.of(
                "adapterId", adapterId,
                "syncStatus", "SUCCESS",
                "sandbox", adapter.getSandboxEnabled(),
                "lastSyncAt", now
        );
    }
}