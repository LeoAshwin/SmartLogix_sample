package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    Page<Report> findByGeneratedById(UUID userId, Pageable pageable);
    Page<Report> findByScope(String scope, Pageable pageable);
}

