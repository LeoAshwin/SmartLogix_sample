package com.cognizant.smartlogix.service.impl;

import com.cognizant.smartlogix.dto.manifest.*;
import com.cognizant.smartlogix.exception.manifest.EntityNotFoundException;
import com.cognizant.smartlogix.model.Manifest;
import com.cognizant.smartlogix.repository.ManifestRepository;
import com.cognizant.smartlogix.repository.RouteLegRepository;
import com.cognizant.smartlogix.service.ManifestService;
import com.cognizant.smartlogix.service.PdfExportService;
import com.cognizant.smartlogix.service.RouteLegService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ManifestService handling core logistics orchestration.
 * Manages route optimization, state transitions, and persistence of shipment data.
 * This service acts as the brain of the SmartLogix system, coordinating between
 * orders, vehicle capacity, and real-time execution.
 */
@Service
public class ManifestServiceImpl implements ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private RouteLegRepository routeLegRepository;

    @Autowired
    private RouteLegService routeLegService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private ObjectMapper objectMapper;

    // Depot Coordinates (Chennai Hub)
    private static final double DEPOT_LAT = 13.0067;
    private static final double DEPOT_LON = 80.2206;
    private static final double AVG_SPEED_KMH = 30.0;
    private static final int SERVICE_TIME_MINS = 5;

    /**
     * Primary logic for generating a new shipment manifest.
     * It validates order coordinates, enforces vehicle weight constraints, and
     * calculates a deterministic sequence of stops with estimated arrival times.
     */
    @Override
    @Transactional
    public ManifestResponseDTO generateDeterministicManifest(ManifestRequestDTO request) {
        validateCoordinates(request.orders());

        Manifest manifest = Manifest.builder()
                .depotId(request.depotId())
                .vehicleId(request.vehicleId())
                .driverId(request.driverId())
                .date(request.scheduledDate())
                .status("OPTIMIZED")
                .build();

        // Standard start time at 08:00 AM
        LocalDateTime currentTime = LocalDateTime.of(request.scheduledDate(), LocalTime.of(8, 0));
        manifest.setStartAt(currentTime);

        List<StopDTO> stops = new ArrayList<>();
        double currentLat = DEPOT_LAT;
        double currentLon = DEPOT_LON;
        double currentWeight = 0.0;
        int sequence = 1;

        for (OrderInputDTO order : request.orders()) {
            if (order.weight() == null) throw new IllegalArgumentException("Weight missing for order: " + order.orderId());
            if (currentWeight + order.weight() > request.maxCapacityKg()) continue;

            double distance = calculateHaversine(currentLat, currentLon, order.lat(), order.lng());
            currentTime = currentTime.plusMinutes((long) ((distance / AVG_SPEED_KMH) * 60));

            stops.add(new StopDTO(order.orderId(), sequence++, currentTime.toString(),
                    "Weight: " + order.weight() + "kg", order.lat(), order.lng(), "PENDING", null));

            currentWeight += order.weight();
            currentLat = order.lat();
            currentLon = order.lng();
            currentTime = currentTime.plusMinutes(SERVICE_TIME_MINS);
        }

        return finalizeManifest(manifest, stops, currentLat, currentLon, currentTime);
    }

    /**
     * Handles manual route adjustments from the dispatcher.
     * It recalculates the entire schedule based on a new stop sequence,
     * provided the trip has not already commenced.
     */
    @Override
    @Transactional
    public ManifestResponseDTO updateManifestManualOverride(Long manifestId, List<StopDTO> stops) {
        Manifest manifest = findManifest(manifestId);

        if (isExecutionLocked(manifest.getStatus())) {
            throw new IllegalStateException("Updates prohibited: Trip has already commenced.");
        }

        LocalDateTime currentTime = manifest.getStartAt();
        double currentLat = DEPOT_LAT;
        double currentLon = DEPOT_LON;
        List<StopDTO> updatedStops = new ArrayList<>();

        for (StopDTO stop : stops) {
            double distance = calculateHaversine(currentLat, currentLon, stop.latitude(), stop.longitude());
            currentTime = currentTime.plusMinutes((long) ((distance / AVG_SPEED_KMH) * 60));

            updatedStops.add(new StopDTO(stop.fulfillmentId(), stop.sequence(), currentTime.toString(),
                    stop.handlingInstructions(), stop.latitude(), stop.longitude(), stop.status(), stop.actualArrivalTime()));

            currentLat = stop.latitude();
            currentLon = stop.longitude();
            currentTime = currentTime.plusMinutes(SERVICE_TIME_MINS);
        }

        manifest.setStatus("MANUALLY_OPTIMIZED");
        return finalizeManifest(manifest, updatedStops, currentLat, currentLon, currentTime);
    }

    /**
     * Internal helper to wrap up manifest creation.
     * Calculates the return leg to the depot, serializes stop data to JSON,
     * and triggers the synchronization with the RouteLeg micro-service.
     */
    private ManifestResponseDTO finalizeManifest(Manifest manifest, List<StopDTO> stops, double lastLat, double lastLon, LocalDateTime time) {
        double returnDist = calculateHaversine(lastLat, lastLon, DEPOT_LAT, DEPOT_LON);
        manifest.setEndAt(time.plusMinutes((long) ((returnDist / AVG_SPEED_KMH) * 60)));
        manifest.setStopsJson(serializeStops(stops));

        Manifest saved = manifestRepository.save(manifest);

        // Sync with RouteLeg module
        routeLegService.generateLegsForManifest(saved.getManifestId(), saved.getStopsJson());

        return mapToResponseDTO(saved);
    }

    /**
     * Real-time update method called when a driver completes a delivery.
     * Updates individual stop status, records actual arrival time, and checks
     * if the entire manifest is now complete.
     */
    @Override
    @Transactional
    public ManifestResponseDTO markStopAsCompleted(Long manifestId, Long fulfillmentId) {
        Manifest manifest = findManifest(manifestId);
        List<StopDTO> stops = deserializeStops(manifest.getStopsJson());

        stops.stream()
                .filter(s -> s.fulfillmentId().equals(fulfillmentId))
                .findFirst()
                .ifPresent(s -> {
                    int idx = stops.indexOf(s);
                    stops.set(idx, new StopDTO(s.fulfillmentId(), s.sequence(), s.estimatedArrivalTime(),
                            s.handlingInstructions(), s.latitude(), s.longitude(), "COMPLETED", LocalDateTime.now().toString()));
                    routeLegRepository.updateStatusByManifestAndSequence(manifestId, s.sequence(), "COMPLETED");
                });

        manifest.setStopsJson(serializeStops(stops));
        if (stops.stream().allMatch(s -> "COMPLETED".equals(s.status()))) manifest.setStatus("COMPLETED");

        return mapToResponseDTO(manifestRepository.save(manifest));
    }

    /**
     * Removes a manifest and its dependent RouteLeg records.
     * Validates existence before deletion to prevent empty-result exceptions.
     */
    @Override
    @Transactional
    public void deleteManifest(Long id) {
        routeLegService.deleteByManifestId(id);
        if (!manifestRepository.existsById(id)) throw new EntityNotFoundException("Manifest not found.");
        manifestRepository.deleteById(id);
    }

    /**
     * Maps the database Entity to a clean Response DTO for the Frontend/PDF.
     * Includes derived calculations for total trip distance and formatted travel duration.
     */
    private ManifestResponseDTO mapToResponseDTO(Manifest m) {
        List<StopDTO> stops = deserializeStops(m.getStopsJson());

        double totalDist = 0.0;
        if (stops != null && !stops.isEmpty()) {
            double cLat = DEPOT_LAT, cLon = DEPOT_LON;
            for (StopDTO s : stops) {
                totalDist += calculateHaversine(cLat, cLon, s.latitude(), s.longitude());
                cLat = s.latitude(); cLon = s.longitude();
            }
            totalDist += calculateHaversine(cLat, cLon, DEPOT_LAT, DEPOT_LON);
        }

        String timeDisplay = "0h 0m";
        if (m.getStartAt() != null && m.getEndAt() != null) {
            long totalMins = java.time.Duration.between(m.getStartAt(), m.getEndAt()).toMinutes();
            long hours = totalMins / 60;
            long mins = totalMins % 60;
            timeDisplay = hours + "h " + mins + "m";
        }

        return new ManifestResponseDTO(
                m.getManifestId(),
                m.getVehicleId(),
                m.getStatus(),
                String.valueOf(m.getDate()),
                stops,
                Math.round(totalDist * 100.0) / 100.0,
                timeDisplay
        );
    }

    /**
     * Mathematical implementation of the Haversine formula to calculate
     * distance between coordinates. Uses a 1.2x multiplier to simulate
     * actual road distance (Road Curvature Factor).
     */
    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1), dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(dLon / 2), 2);
        return (6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 1.2;
    }

    /**
     * Standard fetch-and-validate helper.
     * Centralizes the manifest lookup logic to ensure consistent exception messaging.
     */
    private Manifest findManifest(Long id) {
        return manifestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Manifest not found."));
    }

    /**
     * Utilities for converting Stop lists to and from JSON format.
     * Essential for storing complex stop objects within a single relational database column.
     */
    private String serializeStops(List<StopDTO> stops) {
        try { return objectMapper.writeValueAsString(stops); }
        catch (JsonProcessingException e) { throw new RuntimeException("Serialization error"); }
    }

    private List<StopDTO> deserializeStops(String json) {
        try { return objectMapper.readValue(json, new TypeReference<>() {}); }
        catch (JsonProcessingException e) { throw new RuntimeException("Deserialization error"); }
    }

    /**
     * Geographical validator to ensure latitude and longitude inputs
     * fall within valid global ranges.
     */
    private void validateCoordinates(List<OrderInputDTO> orders) {
        orders.forEach(o -> {
            if (Math.abs(o.lat()) > 90 || Math.abs(o.lng()) > 180) throw new IllegalArgumentException("Invalid coordinates");
        });
    }

    /**
     * State management guard. Prevents modifications to shipment routes
     * once the execution phase (Dispatch/Started) has begun.
     */
    private boolean isExecutionLocked(String status) {
        return List.of("STARTED", "COMPLETED", "CANCELLED").contains(status);
    }

    /**
     * Moves the manifest into the dispatch phase.
     * Indicates that the manifest is finalized and ready for driver pickup.
     */
    @Override
    @Transactional
    public ManifestResponseDTO dispatchManifest(Long id) {
        Manifest m = findManifest(id);
        m.setStatus("DISPATCHED");
        return mapToResponseDTO(manifestRepository.save(m));
    }

    /**
     * Marks the official start of the trip.
     * Triggers a status change for all associated route legs to 'IN_PROGRESS'.
     */
    @Override
    @Transactional
    public ManifestResponseDTO startTrip(Long id) {
        Manifest m = findManifest(id);
        m.setStatus("STARTED");
        routeLegRepository.findByManifestId(id).forEach(l -> { l.setStatus("IN_PROGRESS"); routeLegRepository.save(l); });
        return mapToResponseDTO(manifestRepository.save(m));
    }

    /**
     * Terminal action to stop a manifest execution.
     * Synchronizes the cancellation status across both Manifest and RouteLeg tables.
     */
    @Override
    @Transactional
    public void cancelManifest(Long id) {
        Manifest m = findManifest(id);
        m.setStatus("CANCELLED");
        manifestRepository.save(m);
        routeLegRepository.findByManifestId(id).forEach(l -> { l.setStatus("CANCELLED"); routeLegRepository.save(l); });
    }

    /**
     * Collection of API-facing retrieval methods.
     * Supports fetching by ID, generating PDFs, and filtering by various logistics parameters.
     */
    @Override public ManifestResponseDTO getManifestById(Long id) { return mapToResponseDTO(findManifest(id)); }
    @Override public byte[] exportManifestToPdf(Long id) { return pdfExportService.generateManifestPdf(getManifestById(id)); }
    @Override public List<ManifestResponseDTO> getManifestsByStatus(String s) { return manifestRepository.findByStatus(s).stream().map(this::mapToResponseDTO).toList(); }
    @Override public List<ManifestResponseDTO> getManifestsByVehicle(Long v) { return manifestRepository.findByVehicleId(v).stream().map(this::mapToResponseDTO).toList(); }
    @Override public List<ManifestResponseDTO> searchManifests(String s, Long d, LocalDate dt) {
        List<Manifest> m = (s != null) ? manifestRepository.findByStatus(s) : (dt != null) ? manifestRepository.findByDate(dt) : (d != null) ? manifestRepository.findByDriverId(d) : manifestRepository.findAll();
        return m.stream().map(this::mapToResponseDTO).toList();
    }
}