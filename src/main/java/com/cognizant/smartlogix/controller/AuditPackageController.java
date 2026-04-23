package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.AuditPackageRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.AuditPackageResponseDTO;
import com.cognizant.smartlogix.service.AuditPackageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-packages")
public class AuditPackageController {

    private final AuditPackageService service;

    public AuditPackageController(AuditPackageService service) {
        this.service = service;
    }

    /** Audit records are infrastructure-level — admin and manager only. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER')")
    public AuditPackageResponseDTO create(@RequestBody AuditPackageRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER')")
    public List<AuditPackageResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER')")
    public AuditPackageResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER')")
    public AuditPackageResponseDTO update(
            @PathVariable Long id,
            @RequestBody AuditPackageRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_MANAGER')")
    public AuditPackageResponseDTO patch(
            @PathVariable Long id,
            @RequestBody AuditPackageRequestDTO dto) {
        return service.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}