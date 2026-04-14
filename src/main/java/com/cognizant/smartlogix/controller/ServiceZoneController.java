package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateServiceZoneRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.dto.response.ServiceZoneResponse;
import com.cognizant.smartlogix.model.entity.ServiceZone;
import com.cognizant.smartlogix.model.enums.ServiceZoneStatus;
import com.cognizant.smartlogix.dto.mapper.ServiceZoneMapper;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.repository.ServiceZoneRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * ServiceZone controller â€” thin controller directly using repository for
 * simple CRUD operations (no complex business logic requires a service layer here).
 */
@RestController
@RequestMapping("/zones")
@Tag(name = "Service Zones", description = "Service zone and SLA configuration")
public class ServiceZoneController {

    private final ServiceZoneRepository serviceZoneRepository;
    private final ServiceZoneMapper serviceZoneMapper;

    public ServiceZoneController(ServiceZoneRepository serviceZoneRepository,
                                  ServiceZoneMapper serviceZoneMapper) {
        this.serviceZoneRepository = serviceZoneRepository;
        this.serviceZoneMapper     = serviceZoneMapper;
    }

    @Operation(summary = "Create service zone")
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceZoneResponse>> create(
            @Valid @RequestBody CreateServiceZoneRequest request) {
        if (serviceZoneRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("ServiceZone", "name", request.getName());
        }
        ServiceZone zone = ServiceZone.builder()
                .name(request.getName())
                .polygonGeoJson(request.getPolygonGeoJson())
                .postalCodesJson(request.getPostalCodesJson())
                .slaConfigJson(request.getSlaConfigJson())
                .capacityPerSlot(request.getCapacityPerSlot())
                .timeZone(request.getTimeZone())
                .status(ServiceZoneStatus.ACTIVE)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Zone created", serviceZoneMapper.toResponse(serviceZoneRepository.save(zone))));
    }

    @Operation(summary = "Get zone by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceZoneResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(serviceZoneMapper.toResponse(
                serviceZoneRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("ServiceZone", "id", id)))));
    }

    @Operation(summary = "List zones (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ServiceZoneResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(serviceZoneRepository.findAll(PageRequest.of(page, size))
                        .map(serviceZoneMapper::toResponse))));
    }

    @Operation(summary = "Activate zone")
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<ServiceZoneResponse>> activate(@PathVariable UUID id) {
        ServiceZone zone = serviceZoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceZone", "id", id));
        zone.setStatus(ServiceZoneStatus.ACTIVE);
        return ResponseEntity.ok(ApiResponse.ok("Zone activated", serviceZoneMapper.toResponse(serviceZoneRepository.save(zone))));
    }

    @Operation(summary = "Deactivate zone")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ServiceZoneResponse>> deactivate(@PathVariable UUID id) {
        ServiceZone zone = serviceZoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceZone", "id", id));
        zone.setStatus(ServiceZoneStatus.INACTIVE);
        return ResponseEntity.ok(ApiResponse.ok("Zone deactivated", serviceZoneMapper.toResponse(serviceZoneRepository.save(zone))));
    }
}

