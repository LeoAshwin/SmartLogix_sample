package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.KpiRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.KpiResponseDTO;
import com.cognizant.smartlogix.model.data.Kpi;
import com.cognizant.smartlogix.service.KpiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kpis")
public class KpiController {

    private final KpiService service;

    public KpiController(KpiService service) {
        this.service = service;
    }

    // ✅ Store KPI
    @PostMapping
    public KpiResponseDTO create(@RequestBody KpiRequestDTO dto) {

        Kpi kpi = new Kpi();
        kpi.setName(dto.name());
        kpi.setValue(dto.value());
        kpi.setUnit(dto.unit());
        kpi.setRecordedAt(dto.recordedAt());

        Kpi saved = service.createKpi(kpi);

        return new KpiResponseDTO(
                saved.getKpiId(),
                saved.getName(),
                saved.getValue(),
                saved.getUnit(),
                saved.getRecordedAt()
        );
    }

    // ✅ Fetch stored KPIs
    @GetMapping
    public List<KpiResponseDTO> getAll() {
        return service.getAllKpis()
                .stream()
                .map(k -> new KpiResponseDTO(
                        k.getKpiId(),
                        k.getName(),
                        k.getValue(),
                        k.getUnit(),
                        k.getRecordedAt()
                ))
                .toList();
    }

    // ✅ DASHBOARD KPIs (DUMMY FEATURE)
    @GetMapping("/dashboard")
    public Map<String, Object> dashboardKpis() {
        return service.getComputedKpis();
    }
}