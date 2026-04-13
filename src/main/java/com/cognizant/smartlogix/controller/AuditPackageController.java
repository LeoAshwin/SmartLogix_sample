package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.OpsTrace.request.AuditPackageRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.AuditPackageResponseDTO;
import com.cognizant.smartlogix.model.data.AuditPackage;
import com.cognizant.smartlogix.service.AuditPackageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit-packages")
public class AuditPackageController {

    private final AuditPackageService service;

    public AuditPackageController(AuditPackageService service) {
        this.service = service;
    }

    // ✅ Create audit package
    @PostMapping
    public AuditPackageResponseDTO create(
            @RequestBody AuditPackageRequestDTO dto) {

        AuditPackage auditPackage = new AuditPackage();
        auditPackage.setPeriodStart(dto.periodStart());
        auditPackage.setPeriodEnd(dto.periodEnd());
        auditPackage.setGeneratedAt(dto.generatedAt());
        auditPackage.setPackageUri(dto.packageUri());

        AuditPackage saved = service.createAuditPackage(auditPackage);

        return new AuditPackageResponseDTO(
                saved.getPackageId(),
                saved.getPeriodStart(),
                saved.getPeriodEnd(),
                saved.getGeneratedAt(),
                saved.getPackageUri()
        );
    }

    // ✅ Get all audit packages
    @GetMapping
    public List<AuditPackageResponseDTO> getAll() {
        return service.getAllAuditPackages()
                .stream()
                .map(a -> new AuditPackageResponseDTO(
                        a.getPackageId(),
                        a.getPeriodStart(),
                        a.getPeriodEnd(),
                        a.getGeneratedAt(),
                        a.getPackageUri()))
                .toList();
    }

    // ✅ Get dummy audit contents
    @GetMapping("/{id}/contents")
    public Map<String, Object> getContents(@PathVariable Long id) {
        return service.getAuditContents(id);
    }
}