package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.AuditPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditPackageRepository
        extends JpaRepository<AuditPackage, Long> {
}