package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.KpiRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.KpiResponseDTO;
import com.cognizant.smartlogix.service.KpiService;
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

    // ✅ CREATE KPI
    @PostMapping
    public KpiResponseDTO createKpi(@RequestBody KpiRequestDTO request) {
        return kpiService.create(request);
    }

    // ✅ GET ALL KPIs
    @GetMapping
    public List<KpiResponseDTO> getAllKpis() {
        return kpiService.getAll();
    }

    // ✅ GET KPI BY ID
    @GetMapping("/{id}")
    public KpiResponseDTO getKpiById(@PathVariable Long id) {
        return kpiService.getById(id);
    }

    // ✅ PARTIAL UPDATE (PATCH)
    @PatchMapping("/{id}")
    public KpiResponseDTO updateKpiPartially(
            @PathVariable Long id,
            @RequestBody KpiRequestDTO request) {
        return kpiService.patch(id, request);
    }

    // ✅ DELETE KPI
    @DeleteMapping("/{id}")
    public void deleteKpi(@PathVariable Long id) {
        kpiService.delete(id);
    }

    // ✅ KPI DASHBOARD (PDF FEATURE)
    @GetMapping("/dashboard")
    public Map<String, Object> getKpiDashboard() {
        return kpiService.getDashboard();
    }
}