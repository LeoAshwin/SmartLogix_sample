package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiRepository
        extends JpaRepository<Kpi, Long> {
}