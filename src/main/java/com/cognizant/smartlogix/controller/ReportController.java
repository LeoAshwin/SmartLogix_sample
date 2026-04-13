package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.ReportRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.ReportResponseDTO;
import com.cognizant.smartlogix.model.data.Report;
import com.cognizant.smartlogix.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    // ✅ Create Report
    @PostMapping
    public ReportResponseDTO create(@RequestBody ReportRequestDTO dto) {

        Report report = new Report();
        report.setReportType(dto.reportType());
        report.setGeneratedAt(dto.generatedAt());
        report.setReportUri(dto.reportUri());

        Report saved = service.createReport(report);

        return new ReportResponseDTO(
                saved.getReportId(),
                saved.getReportType(),
                saved.getGeneratedAt(),
                saved.getReportUri()
        );
    }

    // ✅ Fetch all reports
    @GetMapping
    public List<ReportResponseDTO> getAll() {
        return service.getAllReports()
                .stream()
                .map(r -> new ReportResponseDTO(
                        r.getReportId(),
                        r.getReportType(),
                        r.getGeneratedAt(),
                        r.getReportUri()
                ))
                .toList();
    }

    // ✅ Fetch report metrics (DUMMY FEATURE)
    @GetMapping("/{id}/metrics")
    public Map<String, Object> getMetrics(@PathVariable Long id) {
        return service.getReportMetrics(id);
    }
}