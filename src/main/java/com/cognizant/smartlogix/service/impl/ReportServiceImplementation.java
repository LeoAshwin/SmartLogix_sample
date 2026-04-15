package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.OpsTrace.request.ReportRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.ReportResponseDTO;
import com.cognizant.smartlogix.exception.OpsTrace.ReportNotFoundException;
import com.cognizant.smartlogix.model.Report;
import com.cognizant.smartlogix.repository.ReportRepository;
import com.cognizant.smartlogix.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImplementation
        implements ReportService {

    private final ReportRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReportServiceImplementation(ReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public ReportResponseDTO create(ReportRequestDTO dto) {

        Report report = new Report();
        report.setScope(dto.scope());
        report.setParametersJson(dto.parametersJson());
        report.setGeneratedAt(LocalDateTime.now());
        report.setReportUri(dto.reportUri());

        // metrics_json initially empty (real data populated later)
        report.setMetricsJson("{}");

        return toResponse(repository.save(report));
    }

    @Override
    public List<ReportResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReportResponseDTO getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    public ReportResponseDTO update(Long id, ReportRequestDTO dto) {

        Report report = find(id);
        report.setScope(dto.scope());
        report.setParametersJson(dto.parametersJson());
        report.setReportUri(dto.reportUri());

        return toResponse(repository.save(report));
    }

    @Override
    public ReportResponseDTO patch(Long id, ReportRequestDTO dto) {

        Report report = find(id);

        if (dto.scope() != null)
            report.setScope(dto.scope());
        if (dto.parametersJson() != null)
            report.setParametersJson(dto.parametersJson());
        if (dto.reportUri() != null)
            report.setReportUri(dto.reportUri());

        return toResponse(repository.save(report));
    }

    @Override
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Report find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));
    }

    private ReportResponseDTO toResponse(Report report) {
        return new ReportResponseDTO(
                report.getReportId(),
                report.getScope(),
                report.getParametersJson(),
                report.getMetricsJson(),
                report.getGeneratedAt(),
                report.getReportUri()
        );
    }
}