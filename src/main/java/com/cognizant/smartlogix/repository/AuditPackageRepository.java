package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.AuditPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface AuditPackageRepository extends JpaRepository<AuditPackage, UUID> {
    Page<AuditPackage> findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
            LocalDate from, LocalDate to, Pageable pageable);
}

