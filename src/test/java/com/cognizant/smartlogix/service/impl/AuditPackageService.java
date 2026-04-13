package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.model.data.AuditPackage;

import java.util.List;
import java.util.Map;

public interface AuditPackageService {

    AuditPackage createAuditPackage(AuditPackage auditPackage);

    List<AuditPackage> getAllAuditPackages();

    AuditPackage getAuditPackageById(Long id);

    // ✅ Dummy audit content
    Map<String, Object> getAuditContents(Long packageId);
}