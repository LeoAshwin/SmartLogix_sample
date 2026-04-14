package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.mapper.ManifestMapper;
import com.cognizant.smartlogix.dto.request.GenerateManifestRequest;
import com.cognizant.smartlogix.dto.response.ManifestResponse;
import com.cognizant.smartlogix.exception.BusinessException;
import com.cognizant.smartlogix.exception.ResourceNotFoundException;
import com.cognizant.smartlogix.model.entity.*;
import com.cognizant.smartlogix.model.enums.FulfillmentStatus;
import com.cognizant.smartlogix.model.enums.ManifestStatus;
import com.cognizant.smartlogix.repository.*;
import com.cognizant.smartlogix.service.ManifestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ManifestServiceImpl implements ManifestService {

    /** Deterministic average speed profile (km/h) */
    private static final BigDecimal AVG_SPEED_KMH = BigDecimal.valueOf(30);
    /** Average time per stop (minutes) */
    private static final int MINS_PER_STOP = 10;

    private final ManifestRepository manifestRepository;
    private final FulfillmentRepository fulfillmentRepository;
    private final DepotRepository depotRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ManifestMapper manifestMapper;
    private final ObjectMapper objectMapper;

    public ManifestServiceImpl(ManifestRepository manifestRepository,
                                FulfillmentRepository fulfillmentRepository,
                                DepotRepository depotRepository,
                                VehicleRepository vehicleRepository,
                                DriverRepository driverRepository,
                                ManifestMapper manifestMapper,
                                ObjectMapper objectMapper) {
        this.manifestRepository    = manifestRepository;
        this.fulfillmentRepository = fulfillmentRepository;
        this.depotRepository       = depotRepository;
        this.vehicleRepository     = vehicleRepository;
        this.driverRepository      = driverRepository;
        this.manifestMapper        = manifestMapper;
        this.objectMapper          = objectMapper;
    }

    @Override
    public ManifestResponse generate(GenerateManifestRequest request) {
        Depot depot = depotRepository.findById(request.getDepotId())
                .orElseThrow(() -> new ResourceNotFoundException("Depot", "id", request.getDepotId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getDriverId()));

        // Check for existing manifest conflict
        boolean conflict = !manifestRepository.findByDriverIdAndDate(driver.getId(), request.getDate()).isEmpty();
        if (conflict) {
            throw new BusinessException("Driver already has a manifest on " + request.getDate());
        }

        // Fetch eligible PENDING fulfillments for zone + date window
        LocalDateTime windowStart = request.getDate().atStartOfDay();
        LocalDateTime windowEnd   = request.getDate().atTime(23, 59, 59);

        List<Fulfillment> eligible = fulfillmentRepository.findEligibleForManifest(
                FulfillmentStatus.PENDING,
                request.getServiceZoneId(),
                windowStart,
                windowEnd);

        // Deterministic capacity-fit: pack stops until vehicle capacity is full
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        List<Fulfillment> packed = new ArrayList<>();

        for (Fulfillment f : eligible) {
            if (totalWeight.add(f.getPackageWeightKg()).compareTo(vehicle.getCapacityKg()) > 0) break;
            if (totalVolume.add(f.getPackageVolumeM3()).compareTo(vehicle.getCapacityVolumeM3()) > 0) break;
            totalWeight = totalWeight.add(f.getPackageWeightKg());
            totalVolume = totalVolume.add(f.getPackageVolumeM3());
            packed.add(f);
        }

        if (packed.isEmpty()) {
            throw new BusinessException("No eligible PENDING fulfillments for zone " + request.getServiceZoneId() + " on " + request.getDate());
        }

        // Build stopsJson with deterministic ETAs
        List<Map<String, Object>> stops = new ArrayList<>();
        LocalDateTime currentEta = request.getDate().atTime(8, 0); // depot start
        for (int i = 0; i < packed.size(); i++) {
            Fulfillment f = packed.get(i);
            LocalDateTime etaStart = currentEta;
            LocalDateTime etaEnd   = etaStart.plusMinutes(MINS_PER_STOP);
            stops.add(Map.of(
                    "fulfillmentId", f.getId().toString(),
                    "sequence",      i + 1,
                    "etaWindowStart", etaStart.toString(),
                    "etaWindowEnd",   etaEnd.toString()
            ));
            currentEta = etaEnd.plusMinutes(15); // 15-min travel buffer between stops
        }

        String stopsJson = serializeStops(stops);

        // Mark fulfillments as ASSIGNED
        packed.forEach(f -> f.setStatus(FulfillmentStatus.ASSIGNED));
        fulfillmentRepository.saveAll(packed);

        Manifest manifest = Manifest.builder()
                .depot(depot)
                .vehicle(vehicle)
                .driver(driver)
                .date(request.getDate())
                .startAt(request.getDate().atTime(8, 0))
                .stopsJson(stopsJson)
                .status(ManifestStatus.DRAFT)
                .build();

        return manifestMapper.toResponse(manifestRepository.save(manifest));
    }

    @Override
    @Transactional(readOnly = true)
    public ManifestResponse findById(UUID id) {
        return manifestMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ManifestResponse> findAll(Pageable pageable) {
        return manifestRepository.findAll(pageable).map(manifestMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManifestResponse> findByDepotAndDate(UUID depotId, LocalDate date) {
        return manifestRepository.findByDepotIdAndDate(depotId, date)
                .stream().map(manifestMapper::toResponse).toList();
    }

    @Override
    public ManifestResponse publish(UUID manifestId) {
        return updateStatus(manifestId, ManifestStatus.DRAFT, ManifestStatus.PUBLISHED);
    }

    @Override
    public ManifestResponse complete(UUID manifestId) {
        return updateStatus(manifestId, ManifestStatus.IN_PROGRESS, ManifestStatus.COMPLETED);
    }

    @Override
    public ManifestResponse cancel(UUID manifestId) {
        Manifest manifest = findEntityById(manifestId);
        if (manifest.getStatus() == ManifestStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel a COMPLETED manifest");
        }
        manifest.setStatus(ManifestStatus.CANCELLED);
        return manifestMapper.toResponse(manifestRepository.save(manifest));
    }

    @Override
    public ManifestResponse reassignDriver(UUID manifestId, UUID newDriverId) {
        Manifest manifest = findEntityById(manifestId);
        Driver driver = driverRepository.findById(newDriverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", newDriverId));
        manifest.setDriver(driver);
        return manifestMapper.toResponse(manifestRepository.save(manifest));
    }

    @Override
    public ManifestResponse reassignVehicle(UUID manifestId, UUID newVehicleId) {
        Manifest manifest = findEntityById(manifestId);
        Vehicle vehicle = vehicleRepository.findById(newVehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", newVehicleId));
        manifest.setVehicle(vehicle);
        return manifestMapper.toResponse(manifestRepository.save(manifest));
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private Manifest findEntityById(UUID id) {
        return manifestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manifest", "id", id));
    }

    private ManifestResponse updateStatus(UUID id, ManifestStatus expected, ManifestStatus next) {
        Manifest manifest = findEntityById(id);
        if (manifest.getStatus() != expected) {
            throw new BusinessException("Manifest must be in status " + expected + " to transition to " + next);
        }
        manifest.setStatus(next);
        return manifestMapper.toResponse(manifestRepository.save(manifest));
    }

    private String serializeStops(List<Map<String, Object>> stops) {
        try {
            return objectMapper.writeValueAsString(stops);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Failed to serialize manifest stops: " + e.getMessage());
        }
    }
}

