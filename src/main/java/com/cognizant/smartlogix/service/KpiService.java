package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.OpsTrace.request.KpiRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.KpiResponseDTO;

import java.util.List;
import java.util.Map;

public interface KpiService {

    KpiResponseDTO create(KpiRequestDTO dto);

    List<KpiResponseDTO> getAll();

    KpiResponseDTO getById(Long id);

    KpiResponseDTO update(Long id, KpiRequestDTO dto);

    KpiResponseDTO patch(Long id, KpiRequestDTO dto);

    void delete(Long id);

    Map<String, Object> getDashboard();
}
