package com.cognizant.smartlogix.controller;

import com.cognizant.smartlogix.dto.request.CreateDepotRequest;
import com.cognizant.smartlogix.dto.response.ApiResponse;
import com.cognizant.smartlogix.dto.response.DepotResponse;
import com.cognizant.smartlogix.dto.response.PagedResponse;
import com.cognizant.smartlogix.model.entity.Depot;
import com.cognizant.smartlogix.dto.mapper.DepotMapper;
import com.cognizant.smartlogix.exception.DuplicateResourceException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.repository.DepotRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/depots")
@Tag(name = "Depots", description = "Physical facility and depot management")
public class DepotController {

    private final DepotRepository depotRepository;
    private final DepotMapper depotMapper;

    public DepotController(DepotRepository depotRepository, DepotMapper depotMapper) {
        this.depotRepository = depotRepository;
        this.depotMapper = depotMapper;
    }

    @Operation(summary = "Create depot")
    @PostMapping
    public ResponseEntity<ApiResponse<DepotResponse>> create(@Valid @RequestBody CreateDepotRequest request) {
        if (depotRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Depot", "name", request.getName());
        }
        Depot depot = Depot.builder()
                .name(request.getName())
                .addressJson(request.getAddressJson())
                .timeZone(request.getTimeZone())
                .status("ACTIVE")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Depot created", depotMapper.toResponse(depotRepository.save(depot))));
    }

    @Operation(summary = "Get depot by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepotResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(depotMapper.toResponse(
                depotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Depot", "id", id)))));
    }

    @Operation(summary = "List depots (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<DepotResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                PagedResponse.of(depotRepository.findAll(PageRequest.of(page, size))
                        .map(depotMapper::toResponse))));
    }
}

