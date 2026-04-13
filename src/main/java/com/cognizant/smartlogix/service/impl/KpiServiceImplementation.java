package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.exception.OpsTrace.KpiNotFoundException;
import com.cognizant.smartlogix.model.data.Kpi;
import com.cognizant.smartlogix.repository.KpiRepository;
import com.cognizant.smartlogix.service.KpiService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KpiServiceImplementation implements KpiService {

    private final KpiRepository repository;

    public KpiServiceImplementation(KpiRepository repository) {
        this.repository = repository;
    }

    @Override
    public Kpi createKpi(Kpi kpi) {
        return repository.save(kpi);
    }

    @Override
    public List<Kpi> getAllKpis() {
        return repository.findAll();
    }

    @Override
    public Kpi getKpiById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new KpiNotFoundException(id));
    }

    // ✅ DUMMY KPI COMPUTATION (as per requirement)
    @Override
    public Map<String, Object> getComputedKpis() {

        return Map.of(
                "onTimeDeliveryRate", "95%",
                "failedAttempts", 3,
                "averageDeliveryTimeMinutes", 42,
                "capacityUtilization", "80%"
        );
    }
}