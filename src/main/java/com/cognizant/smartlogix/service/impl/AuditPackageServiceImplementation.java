package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.OpsTrace.request.AuditPackageRequestDTO;
import com.cognizant.smartlogix.dto.OpsTrace.response.AuditPackageResponseDTO;
import com.cognizant.smartlogix.exception.OpsTrace.AuditPackageNotFoundException;
import com.cognizant.smartlogix.model.AuditPackage;
import com.cognizant.smartlogix.repository.AuditPackageRepository;
import com.cognizant.smartlogix.service.AuditPackageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditPackageServiceImplementation
        implements AuditPackageService {

    private final AuditPackageRepository repository;

    public AuditPackageServiceImplementation(AuditPackageRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditPackageResponseDTO create(AuditPackageRequestDTO dto) {

        AuditPackage ap = new AuditPackage();
        ap.setPeriodStart(dto.periodStart());
        ap.setPeriodEnd(dto.periodEnd());
        ap.setContentsJson(dto.contentsJson());
        ap.setPackageUri(dto.packageUri());
        ap.setGeneratedAt(LocalDateTime.now());

        return toResponse(repository.save(ap));
    }

    @Override
    public List<AuditPackageResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AuditPackageResponseDTO getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    public AuditPackageResponseDTO update(Long id, AuditPackageRequestDTO dto) {

        AuditPackage ap = find(id);
        ap.setPeriodStart(dto.periodStart());
        ap.setPeriodEnd(dto.periodEnd());
        ap.setContentsJson(dto.contentsJson());
        ap.setPackageUri(dto.packageUri());

        return toResponse(repository.save(ap));
    }

    @Override
    public AuditPackageResponseDTO patch(Long id, AuditPackageRequestDTO dto) {

        AuditPackage ap = find(id);

        if (dto.periodStart() != null)
            ap.setPeriodStart(dto.periodStart());
        if (dto.periodEnd() != null)
            ap.setPeriodEnd(dto.periodEnd());
        if (dto.contentsJson() != null)
            ap.setContentsJson(dto.contentsJson());
        if (dto.packageUri() != null)
            ap.setPackageUri(dto.packageUri());

        return toResponse(repository.save(ap));
    }

    @Override
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private AuditPackage find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AuditPackageNotFoundException(id));
    }

    private AuditPackageResponseDTO toResponse(AuditPackage ap) {
        return new AuditPackageResponseDTO(
                ap.getPackageId(),
                ap.getPeriodStart(),
                ap.getPeriodEnd(),
                ap.getContentsJson(),
                ap.getGeneratedAt(),
                ap.getPackageUri()
        );
    }
}