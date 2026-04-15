package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.dto.OpsTrace.request.AuditPackageRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.AuditPackageResponseDTO;

import java.util.List;

public interface AuditPackageService {

    AuditPackageResponseDTO create(AuditPackageRequestDTO dto);

    List<AuditPackageResponseDTO> getAll();

    AuditPackageResponseDTO getById(Long id);

    AuditPackageResponseDTO update(Long id, AuditPackageRequestDTO dto);

    AuditPackageResponseDTO patch(Long id, AuditPackageRequestDTO dto);

    void delete(Long id);
}
