package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.ReportRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.ReportResponseDTO;
import com.cognizant.smartlogix.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    /** Logistics manager or admin generates a new report. */
    @PostMapping
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public ReportResponseDTO create(@RequestBody ReportRequestDTO dto) {
        return service.create(dto);
    }

    /** Finance officer and dispatcher need read access for reconciliation / planning. */
    @GetMapping
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN','DISPATCHER','FINANCE_OFFICER')")
    public List<ReportResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN','DISPATCHER','FINANCE_OFFICER')")
    public ReportResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /** Full update of a report. */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public ReportResponseDTO update(
            @PathVariable Long id,
            @RequestBody ReportRequestDTO dto) {
        return service.update(id, dto);
    }

    /** Partial update. */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public ReportResponseDTO patch(
            @PathVariable Long id,
            @RequestBody ReportRequestDTO dto) {
        return service.patch(id, dto);
    }

    /** Delete report — manager or admin. */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOGISTICS_MANAGER','ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}