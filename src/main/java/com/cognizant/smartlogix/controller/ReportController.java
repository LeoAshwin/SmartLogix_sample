package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.ReportRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.ReportResponseDTO;
import com.cognizant.smartlogix.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @PostMapping
    public ReportResponseDTO create(@RequestBody ReportRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<ReportResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReportResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ReportResponseDTO update(
            @PathVariable Long id,
            @RequestBody ReportRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    public ReportResponseDTO patch(
            @PathVariable Long id,
            @RequestBody ReportRequestDTO dto) {
        return service.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}