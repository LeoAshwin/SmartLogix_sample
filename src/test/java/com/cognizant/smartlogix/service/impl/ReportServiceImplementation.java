package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.exception.OpsTrace.ReportNotFoundException;
import com.cognizant.smartlogix.model.data.Report;
import com.cognizant.smartlogix.repository.ReportRepository;
import com.cognizant.smartlogix.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImplementation implements ReportService {

    private final ReportRepository repository;

    public ReportServiceImplementation(ReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Report createReport(Report report) {
        return repository.save(report);
    }

    @Override
    public List<Report> getAllReports() {
        return repository.findAll();
    }

    @Override
    public Report getReportById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));
    }

    // ✅ DUMMY REPORT METRICS (as per requirement)
    @Override
    public Map<String, Object> getReportMetrics(Long reportId) {

        return Map.of(
                "totalDeliveries", 120,
                "onTimeDeliveries", 114,
                "failedDeliveries", 6,
                "averageDeliveryTimeMinutes", 42,
                "costPerDelivery", 85.50,
                "currency", "INR",
                "reportValidated", true
        );
    }
}