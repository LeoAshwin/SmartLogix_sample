package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.exception.OpsTrace.AuditPackageNotFoundException;
import com.cognizant.smartlogix.model.data.AuditPackage;
import com.cognizant.smartlogix.service.AuditPackageService;
import com.cognizant.smartlogix.repository.AuditPackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuditPackageServiceImplementation
        implements AuditPackageService {

    private final AuditPackageRepository repository;

    public AuditPackageServiceImplementation(
            AuditPackageRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditPackage createAuditPackage(AuditPackage auditPackage) {

        // ✅ Dummy audit content populated here
        auditPackage.setContentsJson("""
            {
              "totalDeliveries": 200,
              "successfulDeliveries": 190,
              "failedDeliveries": 10,
              "exceptionsLogged": 5,
              "regulatoryValidated": true
            }
        """);

        return repository.save(auditPackage);
    }

    @Override
    public List<AuditPackage> getAllAuditPackages() {
        return repository.findAll();
    }

    @Override
    public AuditPackage getAuditPackageById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new AuditPackageNotFoundException(id));
    }

    @Override
    public Map<String, Object> getAuditContents(Long packageId) {

        // ✅ Dummy content (replace later with real analytics)
        return Map.of(
                "auditPackageId", packageId,
                "totalDeliveries", 200,
                "successfulDeliveries", 190,
                "failedDeliveries", 10,
                "exceptions", 5,
                "auditPassed", true
        );
    }
}
