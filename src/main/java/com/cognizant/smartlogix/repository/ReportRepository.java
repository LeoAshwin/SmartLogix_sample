package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.data.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
