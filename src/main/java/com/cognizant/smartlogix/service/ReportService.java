package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.OpsTrace.request.ReportRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.ReportResponseDTO;

import java.util.List;

public interface ReportService {

    ReportResponseDTO create(ReportRequestDTO dto);

    List<ReportResponseDTO> getAll();

    ReportResponseDTO getById(Long id);

    ReportResponseDTO update(Long id, ReportRequestDTO dto);

    ReportResponseDTO patch(Long id, ReportRequestDTO dto);

    void delete(Long id);
}