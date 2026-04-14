package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.entity.Kpi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KpiRepository extends JpaRepository<Kpi, UUID> {
    List<Kpi> findByReportingPeriod(String reportingPeriod);
    Page<Kpi> findByReportingPeriod(String reportingPeriod, Pageable pageable);
}

