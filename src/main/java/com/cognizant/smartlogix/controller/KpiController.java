package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.KpiRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.KpiResponseDTO;
import com.cognizant.smartlogix.service.KpiService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kpis")
public class KpiController {

    private final KpiService kpiService;

    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    /** Logistics Manager and Admin create KPI definitions. */
    @PostMapping
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public KpiResponseDTO createKpi(@RequestBody KpiRequestDTO request) {
        return kpiService.create(request);
    }

    /** Dispatcher also needs KPI visibility for day-to-day ops. */
    @GetMapping
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN','DISPATCHER')")
    public List<KpiResponseDTO> getAllKpis() {
        return kpiService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN','DISPATCHER')")
    public KpiResponseDTO getKpiById(@PathVariable Long id) {
        return kpiService.getById(id);
    }

    /** Partial update of a KPI. */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public KpiResponseDTO updateKpiPartially(
            @PathVariable Long id,
            @RequestBody KpiRequestDTO request) {
        return kpiService.patch(id, request);
    }

    /** Delete a KPI — admin or manager. */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public void deleteKpi(@PathVariable Long id) {
        kpiService.delete(id);
    }

    /** KPI dashboard accessible to dispatcher as well. */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN','DISPATCHER')")
    public Map<String, Object> getKpiDashboard() {
        return kpiService.getDashboard();
    }
}