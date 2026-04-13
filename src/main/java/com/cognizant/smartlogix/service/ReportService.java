package com.cognizant.smartlogix.service;

import com.cognizant.smartlogix.model.data.Report;

import java.util.List;
import java.util.Map;

public interface ReportService {

    Report createReport(Report report);

    List<Report> getAllReports();

    Report getReportById(Long id);

    // ✅ NEW — Dummy Report Metrics
    Map<String, Object> getReportMetrics(Long reportId);
}