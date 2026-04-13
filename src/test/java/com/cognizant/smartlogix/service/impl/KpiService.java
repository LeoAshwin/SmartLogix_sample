package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.model.data.Kpi;

import java.util.List;
import java.util.Map;

public interface KpiService {

    Kpi createKpi(Kpi kpi);

    List<Kpi> getAllKpis();

    Kpi getKpiById(Long id);

    // ✅ NEW — Dummy KPI computation
    Map<String, Object> getComputedKpis();
}