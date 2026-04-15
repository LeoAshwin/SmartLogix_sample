package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.OpsTrace.request.KpiRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.KpiResponseDTO;
import com.cognizant.smartlogix.exception.OpsTrace.KpiNotFoundException;
import com.cognizant.smartlogix.model.Kpi;
import com.cognizant.smartlogix.repository.KpiRepository;
import com.cognizant.smartlogix.service.KpiService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class KpiServiceImplementation implements KpiService {

    private final KpiRepository repository;

    public KpiServiceImplementation(KpiRepository repository) {
        this.repository = repository;
    }

    @Override
    public KpiResponseDTO create(KpiRequestDTO dto) {

        Kpi kpi = new Kpi();
        kpi.setName(dto.name());
        kpi.setDefinition(dto.definition());
        kpi.setTarget(dto.target());
        kpi.setReportingPeriod(dto.reportingPeriod());

        // ✅ KPI starts with no measured value
        kpi.setCurrentValue(BigDecimal.ZERO);

        return toResponse(repository.save(kpi));
    }

    @Override
    public List<KpiResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public KpiResponseDTO getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    public KpiResponseDTO update(Long id, KpiRequestDTO dto) {

        Kpi kpi = find(id);
        kpi.setName(dto.name());
        kpi.setDefinition(dto.definition());
        kpi.setTarget(dto.target());
        kpi.setReportingPeriod(dto.reportingPeriod());

        return toResponse(repository.save(kpi));
    }

    @Override
    public KpiResponseDTO patch(Long id, KpiRequestDTO dto) {

        Kpi kpi = find(id);

        if (dto.name() != null)
            kpi.setName(dto.name());
        if (dto.definition() != null)
            kpi.setDefinition(dto.definition());
        if (dto.target() != null)
            kpi.setTarget(dto.target());
        if (dto.reportingPeriod() != null)
            kpi.setReportingPeriod(dto.reportingPeriod());

        return toResponse(repository.save(kpi));
    }

    @Override
    public void delete(Long id) {
        repository.delete(find(id));
    }

    /**
     * ✅ KPI Dashboard
     * Reads stored KPI values only (NO execution dependency).
     */
    @Override
    public Map<String, Object> getDashboard() {
        return Map.of(
                "kpis", repository.findAll()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private Kpi find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new KpiNotFoundException(id));
    }

    private KpiResponseDTO toResponse(Kpi kpi) {
        return new KpiResponseDTO(
                kpi.getKpiId(),
                kpi.getName(),
                kpi.getDefinition(),
                kpi.getTarget(),        // ✅ BigDecimal
                kpi.getCurrentValue(),  // ✅ BigDecimal
                kpi.getReportingPeriod()
        );
    }
}